/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */


using System;
using System.Diagnostics;
using System.IO;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Linq;
using System.Text;

namespace CodeGen
{
    /// <summary> .Net TSS code generator </summary>
    internal class CGenDotNet : CodeGenBase
    {
        StringBuilder sb;
        string FileName;
        StringCollection autoGenBitfieldNames = new StringCollection();

        /// <remarks> TSS.Net does not use .snips file to customize auto-generated classes.
        /// Instead it relies on the partial classes mechanism available in C#. </remarks>
        public CGenDotNet(string rootDir) : base(rootDir)
        {
            sb = new StringBuilder();
            FileName = rootDir + @"TSS.Net\X_TpmDefs.cs";
        }

        internal override void Generate()
        {
            // Order of the next two calls is important!
            TweakSymDefStructFieldNames(true);

            Write("using System;");
            Write("using System.Runtime.Serialization;");
            Write("using System.Diagnostics.CodeAnalysis;");
            Write("");
            Write("namespace Tpm2Lib");
            TabIn("{");

            foreach (var e in TpmTypes.Get<TpmEnum>())
                GenEnum(e);
            foreach (var bf in TpmTypes.Get<TpmBitfield>())
                GenBitfield(bf);
            GenUnions();
            foreach (var s in TpmTypes.Get<TpmStruct>())
                GenStructure(s);

            GenCommands();
            GenHandleTable();
            
            TabOut("}");

            File.WriteAllText(FileName, GeneratedCode.ToString(), Encoding.ASCII);

            // Write custom dictionary (for FxCop)
            StreamWriter dict = new StreamWriter("CustomDictionary.xml");

            dict.WriteLine("<Dictionary>\n<Words>\n<Recognized>");
            foreach(TpmType tp in TpmTypes.TheTypes)
            {
                dict.WriteLine($"<Word>\"{tp.Name}\"</Word>");
            }
            foreach (var s in TpmTypes.Get<TpmStruct>())
            {
                foreach (var f in s.Fields)
                    dict.WriteLine($"<Word>{f.Name}</Word>");
            }
            foreach (var c in TpmTypes.Constants)
            {
                dict.WriteLine($"<Word>{c.Name}</Word>");
            }
            dict.WriteLine("</Recognized>\n</Words>\n");

            dict.WriteLine("<Acronyms>\n<CasingExceptions>\n");
            foreach (var s in TpmTypes.Get<TpmStruct>())
            {
                foreach (var f in s.Fields.Where(f => char.IsLower(f.Name[0])))
                {
                    dict.WriteLine($"<Acronym>{f.Name}</Acronym>");
                }
            }
            foreach (var c in TpmTypes.Constants)
            {
                string constName = c.Name;
                if (char.IsUpper(constName[0]))
                    continue;
                dict.WriteLine($"<Acronym>{constName}</Acronym>");
            }
            foreach (string bf in autoGenBitfieldNames)
            {
                if (char.IsUpper(bf[0]))
                    continue;
                dict.WriteLine($"<Acronym>{bf}</Acronym>");
            }

            // extra acronyms
            var ea = new string[] { "TPM", "Nv", "Hr", "Pt", "Ht", "Cc", "Se", "Rc", "Eo", "Pt", "St" , "Rh", "Ex", "Cp", 
                                    "Eh", "Sh", "Ph", "Id", "nv", "Eo", "Gt", "Ge", "Lt", "Le", "Cp", "id", "KDFa"};
            foreach (string a in ea)
                dict.WriteLine($"<Acronym>{a}</Acronym>");
            dict.WriteLine("</CasingExceptions>\n</Acronyms>\n");
            dict.WriteLine("</Dictionary>");
            dict.Flush();
            dict.Close();

            // Undo DotNet specific tweaks
            TweakSymDefStructFieldNames(false);
        }

        static void TweakSymDefStructFieldNames(bool dotNet)
        {
            var sd = (TpmStruct)TpmTypes.Lookup("TPMT_SYM_DEF");
            var sdo = (TpmStruct)TpmTypes.Lookup("TPMT_SYM_DEF_OBJECT");
            for (int i = 0; i < sd.Fields.Count; ++i)
            {
                Debug.Assert(sd.Fields[i].Name == sdo.Fields[i].Name);
                if (dotNet)
                    sd.Fields[i].Name = sdo.Fields[i].Name = Helpers.Capitalize(sd.Fields[i].Name);
                else
                    sd.Fields[i].Name = sdo.Fields[i].Name = sd.Fields[i].Name.ToLower();
            }
        }

        void GenEnum(TpmType e, List<TpmNamedConstant> elements, bool needsNone = false)
        {
            WriteComment(e);
            if (e is TpmBitfield)
                Write("[Flags]");
            Write("[DataContract]");
            Write($"[SpecTypeName(\"{e.SpecName}\")]");
            Write($"public enum {e.Name} : {e.GetFinalUnderlyingType().Name}");
            TabIn("{");
            
            if (needsNone)
                Write("None = 0,");

            foreach (TpmNamedConstant nc in elements)
            {
                string commaAfterOld = Separator(nc, elements);
                string comma = nc.Name != nc.OldStyleName ? "," : commaAfterOld;

                WriteComment(nc);
                Write("[EnumMember]");

                string  value = nc.Value,
                        valueComment = "";
                if (nc.SpecValue.Expr != null)
                {
                    // Bitfields keep their member names unchanged, so they do not need the SpecTypeName attr.
                    Write($"[SpecTypeName(\"{nc.SpecName}\")]");

                    uint val = unchecked((uint)nc.NumericValue);
                    valueComment = Expression.IsNumber(nc.SpecValue.Expr) ? "" : " // 0x" + val.ToString("X");
                    if (val >= unchecked((uint)(0x90 << 24)))
                        value = $"unchecked (({e.GetFinalUnderlyingType().Name})({nc.Value}))";
                }
                Write($"{nc.Name} = {value}{comma}{valueComment}");

                if (nc.Name != nc.OldStyleName)
                {
                    Write($"[Obsolete(\"Use {e.Name}.{nc.Name} instead\")]");
                    Write($"{nc.OldStyleName} = {nc.Value}{commaAfterOld}");
                }
            };
            TabOut("}");
        } // GenEnum()

        void GenEnum(TpmEnum e)
        {
            GenEnum(e, e.Members, null == e.Members.FirstOrDefault(x => x.Name == "None"));
        }

        void GenBitfield(TpmBitfield bf)
        {
            GenEnum(bf, GetBifieldElements(bf), true);
        }

        void GenUnions()
        {
            TpmUnion[] unions = TpmTypes.Get<TpmUnion>().ToArray();

            foreach (TpmUnion t in unions)
            {
                string selectorType = t.Members[0].SelectorValue.EnclosingEnum.Name;
                WriteComment(t);
                Write("public interface " + t.Name);
                TabIn("{");
                Write($"{selectorType} GetUnionSelector();");
                TabOut("}");
            }

            Write("public abstract partial class TpmStructureBase");
            TabIn("{");
            Write("Type UnionElementFromSelector(Type unionInterface, object selector)");
            TabIn("{");

            string elseClause = "";
            foreach (TpmUnion t in unions)
            {
                Write($"{elseClause}if (unionInterface == typeof({t.Name}))");
                TabIn("{");
                TpmEnum selectorType = null;
                foreach (UnionMember m in t.Members)
                {
                    if (selectorType == null)
                    {
                        selectorType = m.SelectorValue.EnclosingEnum;
                        Write($"switch (({selectorType.Name})selector)");
                        TabIn("{");
                    }
                    Debug.Assert(selectorType == m.SelectorValue.EnclosingEnum);
                    Write($"case {m.SelectorValue.QualifiedName}: return typeof({m.Type.Name});");
                }
                TabOut("}", false);     // switch
                TabOut("}", false);     // else if
                elseClause = "else ";
            }
            Write("else");
            TabIn("{");
            Write("throw new Exception(\"Unknown union interface type \" + unionInterface.Name);");
            TabOut("}", false); // else
            Write("throw new Exception(\"Unknown selector value\" + selector + \" for \" + unionInterface.Name +  \" union\");");
            TabOut("}", false); // UnionElementFromSelector
            TabOut("}"); // TpmStructureBase
        } // GenUnions()

        void GenMemeberwiseCtorPrototype(string typeName, IEnumerable<StructField> fields)
        {
            foreach (StructField f in fields)
                WriteComment(GetParamComment(f, "_"), false);

            string argList = "";
            // print out the parameters (apart from the counts and selectors, which are auto-derived)
            foreach (StructField f in fields)
                argList += (argList.Length > 0 ? ", " : "") + $"{f.TypeName} _{f.Name}";
            Write($"public {typeName}({argList})", false);
        }

        void GenGetUnionSelector(TpmStruct s, bool implementsUnionInterfaces = true)
        {
            string selType = GetUnionMemberSelectorInfo(s, out string selVal);
            if (selType != null)
            {
                string overrideFlavor = implementsUnionInterfaces ? "virtual" : "override";
                Write("");
                Write($"public {overrideFlavor} {selType} GetUnionSelector() {{ return {selVal}; }}");
            }
        }

        void WriteFieldDef(StructField f, string initializerOrBody = ";")
        {
            Write("[DataMember()]");
            Write($"public {f.TypeName} {f.Name}{initializerOrBody}");
        }

        void GenStructure(TpmStruct s)
        {
            bool hasBase = s.DerivedFrom != null;   // Has a non-trivial base class?
            string className = s.Name;
            string classBases = hasBase ? s.DerivedFrom.Name : "TpmStructureBase";

            // Here "implements" is as opposed to "inherits and overrides"
            bool implementsUnionInterfaces = !s.IsCmdStruct() && !hasBase && s.ContainingUnions.Count > 0;
            Debug.Assert(s.DerivedFrom == null || s.DerivedFrom.ContainingUnions.Count >= s.ContainingUnions.Count);
            if (implementsUnionInterfaces)
            {
                foreach(TpmUnion u in s.ContainingUnions)
                    classBases += ", " + u.Name;
            }

            WriteComment(s);
            Write("[DataContract]");
            var knownTypes = GetKnownTypes(s);
            foreach (TpmType kt in knownTypes)
                Write($"[KnownType(typeof({kt.Name}))]");

            string specName = s.Info.IsRequest() ? s.SpecName.Substring(5).Replace("_REQUEST", "_In")
                            : s.Info.IsResponse() ? s.SpecName.Replace("Response", "_Out")
                            : s.SpecName;
            Write($"[SpecTypeName(\"{specName}\")]");

            Write($"public partial class {className}: {classBases}");
            TabIn("{");

            if (s.DerivedFrom != null)
            {
                Debug.Assert(!s.IsCmdStruct());
                //---------------------------------------
                // Constructors
                var fields = new List<StructField>();
                TpmStruct b = s;
                do {
                    fields.AddRange(b.NonTagFields);
                    b = b.DerivedFrom;
                } while (b != null);

                // Default constructor
                Write($"public {className}() {{}}");
                Write("");

                if (fields.Count != 0)
                {
                    // Copy-constructor
                    WriteLine("public {0}({0} _{0}) : base(_{0}) {{}}", className);
                    Write("");

                    // Member-wise constructor
                    GenMemeberwiseCtorPrototype(className, fields);
                    if (fields.Count == 1)
                    {
                        Write($" : base(_{fields.First().Name}) {{}}");
                    }
                    else
                    {
                        string baseInitList = string.Join(", ", fields.ConvertAll(f => "_" + f.Name));
                        Write("");
                        Write($"    : base({baseInitList})");
                        Write("{}");
                    }
                    Write("");
                }

                GenGetUnionSelector(s, implementsUnionInterfaces);
                GenCloningMethods(s.Name);

                // end of class
                TabOut("}");
                return;
            } // if (s.DerivedFrom != null)

            //
            // Member fields
            //

            bool onlyStaticFields = true;
            int idx = 0;
            foreach (StructField f in s.Fields)
            {
                var tag = f.SizeTagField;
                if (f.SizedField == null)
                {
                    WriteComment(f);
                    WriteConstraints(f);
                }
                onlyStaticFields = false;

                switch(f.MarshalType)
                {
                    case MarshalType.ArrayCount: 
                    case MarshalType.LengthOfStruct:
                        --idx;
                        break;
                    case MarshalType.UnionSelector:
                    {
                        Debug.Assert(f.RelatedUnion.MarshalType == MarshalType.UnionObject); 
                        Debug.Assert(f.RelatedUnion.UnionSelector == f);
                        var unionField = f.RelatedUnion;
                        var u = (TpmUnion)unionField.Type;
                        Write($"[MarshalAs({idx}, MarshalType.UnionSelector)]");
                        TabIn($"public {f.TypeName} {f.Name} {{");
                        if (u.NullSelector == null)
                            Write($"get {{ return {unionField.Name}.GetUnionSelector(); }}");
                        else
                            Write($"get {{ return {unionField.Name} != null ? {unionField.Name}.GetUnionSelector() : {u.NullSelector.QualifiedName}; }}");
                        TabOut("}"); // property
                        break;
                    }
                    case MarshalType.Normal:
                    case MarshalType.SizedStruct:
                    {
                        if (tag == null)
                            Write($"[MarshalAs({idx})]");
                        else
                            Write($"[MarshalAs({idx}, MarshalType.SizedStruct, \"{tag.Name}\", {tag.Type.GetSize()})]");
                        WriteFieldDef(f, " { get; set; }");
                        break;
                    }
                    case MarshalType.UnionObject:
                    {
                        UnionField fx = (UnionField) f;
                        Write($"[MarshalAs({idx}, MarshalType.Union, \"{fx.UnionSelector.Name}\")]");
                        WriteFieldDef(f, " { get; set; }");
                        break;
                    }
                    case MarshalType.VariableLengthArray:
                    case MarshalType.SpecialVariableLengthArray:
                    {
                        string marshalType = Enum.GetName(typeof(MarshalType), f.MarshalType);
                        Write($"[MarshalAs({idx}, MarshalType.{marshalType}, \"{tag.Name}\", {tag.Type.GetSize()})]");
                        WriteFieldDef(f);
                        break;
                    }
                    case MarshalType.EncryptedVariableLengthArray:
                    {
                        Write($"[MarshalAs({idx}, MarshalType.EncryptedVariableLengthArray)]");
                        WriteFieldDef(f);
                        break;
                    }
                    case MarshalType.ConstantValue:
                    {
                        string val = TargetLang.TranslateConstExpr(f.Domain[0, Constraint.Type.Single]);
                        Write($"[MarshalAs({idx})]");
                        WriteFieldDef(f, $" = {val};");
                        break;
                    }
                    default:
                        throw new Exception();
                }
                ++idx;
            } // foreach field

            if (onlyStaticFields && s.Fields.Count > 0)
            {
                // end of class
                TabOut("}");
                return;
            }

            // Default constructor
            var fieldsToInit = s.NonDefaultInitFields;
            Write("");
            Write($"public {className}()", false);
            if (fieldsToInit.Count() == 0)
            {
                Write(" {}");
            }
            else if (fieldsToInit.Count() == 1)
            {
                var f = fieldsToInit[0];
                Write($" {{ {f.Name} = {f.GetInitVal()}; }}");
            }
            else
            {
                TabIn("{");
                foreach (StructField f in fieldsToInit)
                {
                    Write($"{f.Name} = {f.GetInitVal()};");
                }
                TabOut("}");
            }

            // Copy constructor
            if (!s.Info.IsRequest())
            {
                var fields = s.NonTagFields;
                if (fields.Count() != 0)
                {
                    Write("");
                    Write($"public {className}({className} src)", false);
                    if (fields.Count() == 1 && s.SpecName != "TPM_HANDLE")
                    {
                        string field = fields.First().Name;
                        Write($" {{ {field} = src.{field}; }}");
                    }
                    else
                    {
                        Write("");
                        TabIn("{");
                        foreach (StructField f in fields)
                            Write($"{f.Name} = src.{f.Name};");

                        // special case
                        if (s.SpecName == "TPM_HANDLE")
                        {
                            Write("Auth = src.Auth;");
                            TabIn("if (src.Name != null)");
                            Write("Name = Globs.CopyData(src.Name);");
                            TabOut();
                        }
                        TabOut("}");
                    }
                }
            }

            // Member-wise constructor
            if (!s.Info.IsResponse())
            {
                var fields = s.NonTagFields;
                if (fields.Count() != 0)
                {
                    GenMemeberwiseCtorPrototype(className, fields);
                    if (fields.Count() == 1)
                    {
                        string fName = fields.First().Name;
                        Write($" {{ {fName} = _{fName}; }}");
                    }
                    else
                    {
                        TabIn("{");
                        foreach (StructField f in fields)
                            Write($"{f.Name} = _{f.Name};");
                        TabOut("}");
                    }
                }
            }

            GenGetUnionSelector(s);
            GenCloningMethods(s.Name);
            TabOut("}"); // end of class
        } // GenStructure()

        void GenCloningMethods(string typeName)
        {
            Write("");
            Write($"new public {typeName} Copy() {{ return CreateCopy<{typeName}>(); }}");
            Write("");
            Write("public override TpmStructureBase Clone() { return Copy(); }");
        }

        void GenCommands()
        {
            Write("//-----------------------------------------------------------------------------");
            Write("//------------------------- COMMANDS -----------------------------------------");
            Write("//-----------------------------------------------------------------------------");
            Write("");
            Write("public partial class Tpm2");
            TabIn("{");

            foreach (var req in TpmTypes.Get<TpmStruct>().Where(s => s.Info.IsRequest()))
            {
                string cmdName = GetCommandName(req);
                var resp = GetRespStruct(req);
                var reqFields = req.NonTagFields;
                var respFields = resp.NonTagFields;

                string outputType = "void";
                int numRespParams = respFields.Count();
                if (respFields.Count() != 0)
                {
                    if (respFields.Last().Name == "name")
                        --numRespParams;

                    outputType = respFields[0].TypeName;
                }

                string annotation = req.Comment + "\n\n";
                foreach (var f in reqFields)
                    annotation += GetParamComment(f) + "\n";
                annotation += GetReturnComment(respFields);
                WriteComment(annotation, false);

                // commmand prototype + parameters

                string transCmdName = TargetLang.NameToDotNet(cmdName);

                Write("[TpmCommand]");
                TabIn($"public {outputType} {transCmdName}(");
                bool printComma = false;
                foreach (StructField f in reqFields)
                {
                    WriteComma(ref printComma);
                    Write($"{f.TypeName} {f.Name}", false);
                }
                for (int i = 1; i < numRespParams; ++i)
                {
                    var f = respFields[i];
                    WriteComma(ref printComma);
                    Write($"out {f.TypeName} {f.Name}", false);
                }
                TabOut(")");
                TabIn("{");

                // Create input struct
                string reqStructInitList = string.Join(", ", reqFields.Select(f => f.Name));
                Write($"var req = new {req.Name}({reqStructInitList});");

                // Dispatch the command
                string respType = !resp.Implement ? "EmptyResponse" : resp.Name;
                Write($"var resp = new {respType}();");
                Write($"DispatchMethod(TpmCc.{transCmdName}, req, resp, {req.NumHandles}, {resp.NumHandles});");

                if (numRespParams > 0)
                {
                    if (numRespParams == 1)
                        Write($"return resp.{respFields[0].Name};");
                    else
                    {
                        // Set the return parameters
                        for (int i = 1; i < numRespParams; ++i)
                        {
                            string rfName = respFields[i].Name;
                            Write($"{rfName} = resp.{rfName};");
                        }
                        Write($"return resp.{respFields[0].Name};");
                    }
                }
                TabOut("}");
                continue;         
            }
            TabOut("}");
        } // GenCommands()

        string StripOrigType(string s)
        {
            // remove this if present
            int toRemove = s.LastIndexOf("(Original type");
            if (toRemove == -1) return s;
            // else strip it
            s = s.Substring(0, toRemove);
            s = s.TrimEnd(new char[] {' ', '\n'});
            return s;
        }

        // only length prepended first-in or first-out parms can be encypted.
        [Flags]
        enum ParmCryptInfo
        {
            EncIn2 = 1, 
            EncIn4 = 2, 
            DecOut2 = 4, 
            DecOut4 = 8
        }
        void GenHandleTable()
        {
            Write("//-----------------------------------------------------------------------------");
            Write("//------------------------- COMMAND INFO -----------------------------------");
            Write("//-----------------------------------------------------------------------------");
            Write("public static class CommandInformation");
            TabIn("{");
            Write("public static CommandInfo[] Info = new CommandInfo[]");
            TabIn("{");
            bool printComma = false;
            foreach (var req in TpmTypes.Get<TpmStruct>().Where(s => s.Info.IsRequest()))
            {
                TpmStruct resp = GetRespStruct(req);
                string cmdName = GetCommandName(req);
                string transCmdName = TargetLang.TypeToDotNet(cmdName);
                string reqStructName = "Tpm2" + transCmdName + "Request";

                // encryptable parms?
                ParmCryptInfo cryptInfo = 0;
                
                if (req.Fields.Count > req.NumHandles)
                {
                    // find the first field that is not a handle
                    StructField parm0 = req.Fields[req.NumHandles];
                    if (parm0.MarshalType.IsOneOf(MarshalType.LengthOfStruct, MarshalType.ArrayCount))
                    {
                        string typeName = parm0.Type.StripTypedefs().Name;
                        if (typeName == "uint")
                            cryptInfo |= ParmCryptInfo.EncIn4;
                        else if (typeName == "ushort")
                            cryptInfo |= ParmCryptInfo.EncIn2;
                    }
                    // The logic above takes a dependency on the flattening of tagged structures.
                    // Check if this field is a TPM2B with flattening skipped.
                    if (parm0.Type.SpecName.IsOneOf(TpmTypeTranslations.DontFlatten) && parm0.Type.SpecName.StartsWith("TPM2B"))
                    {
                        cryptInfo |= ParmCryptInfo.EncIn2;
                    }
                }
                if (resp.Fields.Count > resp.NumHandles)
                {
                    // find the first field that is not a handle
                    StructField parm0 = resp.Fields[resp.NumHandles];
                    if (parm0.MarshalType.IsOneOf(MarshalType.LengthOfStruct, MarshalType.ArrayCount))
                    {
                        string typeName = parm0.Type.StripTypedefs().Name;
                        if (typeName == "uint")
                            cryptInfo |= ParmCryptInfo.DecOut4;
                        else if (typeName == "ushort")
                            cryptInfo |= ParmCryptInfo.DecOut2;
                    }
                    // The logic above takes a dependency on the flattening of tagged structures.
                    // Check if this field is a TPM2B with flattening skipped.
                    if (parm0.Type.SpecName.IsOneOf(TpmTypeTranslations.DontFlatten) && parm0.Type.SpecName.StartsWith("TPM2B"))
                    {
                        cryptInfo |= ParmCryptInfo.DecOut2;
                    }
                }

                string handleTypeNames = "";
                // types of input handles
                if (req.NumHandles > 0)
                {
                    for (int j = 0; j < req.NumHandles; j++)
                    {
                        StructField hField = req.Fields[j];
                        string origHandleType = hField.Type.SpecName;
                        handleTypeNames += origHandleType + " ";
                        TpmType tpx = TpmTypes.Lookup(origHandleType);
                    }
                }
                handleTypeNames = handleTypeNames.TrimEnd(new char[] { ' ' });
                handleTypeNames = "\"" + handleTypeNames + "\"";

                string respTypeId = !resp.Implement ? "EmptyResponse"
                                  : resp.Name;

                WriteComma(ref printComma);
                Write($"new CommandInfo(TpmCc.{transCmdName}, {req.NumHandles}, {resp.NumHandles}, {req.NumAuthHandles}, " + 
                      $"typeof({reqStructName}), typeof({respTypeId}), {(uint)cryptInfo}, {handleTypeNames})", false);
            }
            TabOut("};");
            TabOut("}");
        }

        void WriteComma (ref bool printComma)
        {
            if (printComma)
                Write(",");
            else
                printComma = true;
        }

        static void AddConstraint(ref string clause, string name, TpmConstExpr val)
        {
            if (val == null)
                return;
            if (clause != "")
                clause += ", ";
            clause += (name == null ? "" : name + " = ") + 
                      val.NumericValue + "u /*" + val.Expr + "*/";
        }

        void WriteConstraints(StructField f)
        {
            if (f.Domain.Count == 0)
                return;
            string constraint = "";
            if (f.Domain.Count > 1)
            {
                // Here we only expect multiple SingleValue constraints.
                foreach (var val in f.Domain)
                    AddConstraint(ref constraint, null, (val as SingleValue).Value);
                constraint = "Values = new[] {" + constraint + "}";
            }
            else
            {
                AddConstraint(ref constraint, "MinVal", f.Domain[0, Constraint.Type.Min]);
                AddConstraint(ref constraint, "MaxVal", f.Domain[0, Constraint.Type.Max]);
                AddConstraint(ref constraint, "OnlyVal", f.Domain[0, Constraint.Type.Single]);
                Debug.Assert (constraint != "");
            }
            Write($"[Range({constraint})]");
        }

        static List<TpmStruct> GetUnionMemberStructures(UnionField uf)
        {
            var unionList = new List<TpmStruct>();
            foreach (var s in TpmTypes.Get<TpmStruct>().Where(s => !s.IsCmdStruct()))
            {
                foreach (TpmUnion u in s.ContainingUnions)
                {
                    if (u.Name == uf.TypeName)
                        unionList.Add(s);
                }
            }
            return unionList;
        }

        // The serializer needs hints when embedded structs or unions are serialized/deserialized.
        // This function returns the referenced structure types for simple embedded structs and
        // interface members.
        IEnumerable<TpmType> GetKnownTypes(TpmStruct s)
        {
            var containedTypes = new List<TpmType>();
            foreach (StructField f in s.Fields)
            {
                switch (f.MarshalType)
                {
                    case MarshalType.Normal:
                    case MarshalType.SizedStruct:
                    case MarshalType.UnionSelector:
                        if (f.Type is TpmStruct) 
                        {
                            containedTypes.Add(f.Type);
                        }
                        else if (f.Type is TpmTypedef)
                        {
                            var tdt = f.Type.StripTypedefs();
                            if (tdt.IsElementary() || tdt.Name == "KeyBits")
                                continue;
                            if (!containedTypes.Contains(tdt))
                                containedTypes.Add(tdt);
                        }
                        else if (f.Type is TpmEnum || f.Type is TpmBitfield)
                        {
                            if (!containedTypes.Contains(f.Type))
                                containedTypes.Add(f.Type);
                        }
                        break;
                    case MarshalType.UnionObject: 
                        var unionMembers = (f.Type as TpmUnion).Members.Select(um => um.Type);
                        containedTypes = containedTypes.Union(unionMembers).ToList();
                        break;
                    default:
                        continue;
                }
            }
            return containedTypes.OrderBy(t => t.SpecName);
        }

        protected override void WriteComment(string comment, bool wrap = true)
        {
            WriteComment(comment, "/// ", "/// ", "", wrap);
            EmptyLineWritten = true;
        }
    }
}

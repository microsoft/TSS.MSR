/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System.Diagnostics;
using System.Collections.Generic;
using System.Linq;

namespace CodeGen
{
    /// <summary> C++ TSS code generator </summary>
    partial class CGenCpp : CodeGenBase
    {
        // Maps enum type to a map of enumerator names to values
        Dictionary<string, Dictionary<string, string>> EnumMap;

        public CGenCpp(string rootDir) : base(rootDir, @"Src\TpmExtensions.cpp.snips") {}

        internal override void Generate()
        {
            EnumMap = new Dictionary<string, Dictionary<string, string>>();

            GenerateTpmTypesHdr();
            UpdateExistingSource(@"include\TpmTypes.h");

            GenerateTpmCommandPrototypes();
            UpdateExistingSource(@"include\Tpm2.h");

            GenerateTpmTypesCpp();
            UpdateExistingSource(@"Src\TpmTypes.cpp");
        }

        /// <summary> Determines whether this struct is represented as a typedef in C++ </summary>
        static bool IsTypedefStruct(TpmStruct s)
        {
            return s.DerivedFrom != null && s.ContainingUnions.Count == 0;
        }

        static string CtorParamTypeFor(StructField f)
        {
            if (f.IsArray() || !f.IsValueType())
                return $"const {f.TypeName}&";
            return f.TypeName;
        }

        static string TransType(StructField f)
        {
            if (f.MarshalType == MarshalType.UnionObject)
                return $"shared_ptr<{f.TypeName}>";
            return f.TypeName;
        }

        string GetCommandReturnType(CommandFlavor gen, TpmStruct resp, string methodName,
                                    out string returnFieldName)
        {
            returnFieldName = null;
            if (gen == CommandFlavor.AsyncCommand)
                return "void";

            string returnType = "void";
            var respFields = resp.NonTagFields;
            if (ForceJustOneReturnParm.Contains(methodName))
            {
                respFields = respFields.Take(1).ToArray();
            }

            if (respFields.Count() > 1)
                return resp.Name;

            if (respFields.Count() == 1)
            {
                returnFieldName = respFields[0].Name;
                returnType = TransType(respFields[0]);
            }
            return returnType;
        }

        void GenerateTpmTypesHdr()
        {
            foreach (var e in TpmTypes.Get<TpmEnum>())
                GenEnum(e);

            foreach (var bf in TpmTypes.Get<TpmBitfield>())
                GenBitfield(bf);

            WriteComment(AsSummary("Base class for TPM union interfaces"));
            Write("class _DLLEXP_ TpmUnion: public virtual TpmStructure {};");

            foreach (var u in TpmTypes.Get<TpmUnion>())
                GenUnion(u);

            foreach (var s in TpmTypes.Get<TpmStruct>())
                GenStructDecl(s);

            Write("_TPMCPP_END");
        } // GenerateHeader()

        void GenEnum(TpmType e, List<TpmNamedConstant> elements)
        {
            WriteComment(e);
            Write($"struct {e.Name} : public TpmEnum<{e.GetFinalUnderlyingType().Name}>");
            TabIn("{");
            Write($"TPM_ENUM_PROLOGUE({e.Name})");

            var enumVals = new Dictionary<string, string>();
            foreach (var elt in elements)
            {
                WriteComment(AsSummary(elt.Comment));
                string delimiter = Separator(elt, elements);
                Write($"{elt.Name} = {elt.Value}{delimiter}");

                // Do not include artificially added named constants into the name conversion maps
                if (elt.SpecName != null)
                    enumVals[elt.Name] = e is TpmEnum ? ToHex(elt.NumericValue) : elt.Value;

                // Backward compat
                if (elt.Name == "PW")
                {
                    WriteComment(AsSummary("Deprecated: use PW instead"));
                    Write($"RS_PW [[deprecated(\"Use TPM_RH::PW instead\")]] = {elt.Value},");
                }
            }
            Write($"TPM_ENUM_EPILOGUE({e.Name})");
            TabOut("};");
            EnumMap[e.Name] = enumVals;
        } // GenEnum()

        void GenEnum(TpmEnum e)
        {
            GenEnum(e, e.Members);
        }

        void GenBitfield(TpmBitfield bf)
        {
            GenEnum(bf, GetBifieldElements(bf));

        }

        void GenUnion(TpmUnion u)
        {
            if (!u.Implement)
                return;

            WriteComment(u);
            Write("class _DLLEXP_ " + u.Name + ": public virtual TpmUnion");
            TabIn("{");

            // Note: cannot sink GetUnionSelector() to the base TpmUnion interafce in C++,
            // as some unions use different unrelated enums as return types.
            Write($"public: virtual {GetUnionSelectorType(u)} GetUnionSelector() const = 0;");

            Write("public: virtual TpmStructure*  Clone() const { _ASSERT(FALSE); return NULL; };");
            TabOut("};");
        } // GenUnion()

        void GenGetUnionSelector(TpmStruct s)
        {
            string selType = GetUnionMemberSelectorInfo(s, out string selVal);
            if (selType != null)
            {
                WriteComment(AsSummary("TpmUnion method"));
                Write($"{selType} GetUnionSelector() const {{ return {selVal}; }}");
            }
        }

        void GenStructDecl(TpmStruct s)
        {
            bool hasBase = s.DerivedFrom != null;   // Has a non-trivial base class?
            string className = s.Name;

            if (IsTypedefStruct(s))
            {
                Debug.Assert(s.Fields.Count == 0);
                WriteComment(s);
                Write($"typedef {s.DerivedFrom.Name} {className};");
                Write("");
                return;
            }

            string classBases = hasBase ? s.DerivedFrom.Name
                              : !s.IsCmdStruct() ? "TpmStructure"
                              : s.Info.IsRequest() ? "ReqStructure" : "RespStructure";
            string virt = "";
            // If this struct is not derived from another one and is a member of one or more unions,
            // it must implement the corresponding union interfaces
            if (!s.IsCmdStruct() && !hasBase && s.ContainingUnions.Count > 0)
            {
                foreach (var u in s.ContainingUnions)
                    classBases += ", public " + u.Name;
                virt = "virtual ";
            }

            WriteComment(s);
            Write($"class _DLLEXP_ {className} : public {virt}{classBases}");
            Write("{");
            TabIn("public:");

            //
            // Fields
            //
            TpmField conversionSource = null;
            foreach (var f in s.NonSizeFields)
            {
                if (f.MarshalType == MarshalType.ConstantValue)
                    // No member field for a constant tag
                    continue;

                WriteComment(f);
                if (f.MarshalType == MarshalType.UnionSelector)
                {
                    var unionField = f.RelatedUnion;
                    var u = (TpmUnion)unionField.Type;
                    Write($"public: {f.TypeName} {f.Name}() const {{ ", false);
                    if (u.NullSelector == null)
                        Write($"return {unionField.Name}->GetUnionSelector(); }}");
                    else
                    {
                        Write($"return {unionField.Name} ? {unionField.Name}->GetUnionSelector()" +
                                                                $" : {u.NullSelector.QualifiedName}; }}");
                    }
                    continue;
                }

                Write($"{TransType(f)} {f.Name};");
            }

            TabOut("");
            TabIn("public:");

            //
            // Default constructor
            //
            var fieldsToInit = s.NonDefaultInitFields;
            Write($"{className}()", false);
            if (fieldsToInit.Count() == 0)
                Write(" {}");
            else if (fieldsToInit.Count() == 1)
                Write($" {{ {fieldsToInit[0].Name} = {fieldsToInit[0].GetInitVal()}; }}");
            else
            {
                TabIn("{");
                foreach (StructField f in fieldsToInit)
                    Write($"{f.Name} = {f.GetInitVal()};");
                TabOut("}");
            }

            //
            // Member-wise constructor
            //
            var ctorParams = s.FieldHolder.NonTagFields;
            if (!s.Info.IsResponse() && ctorParams.Count() != 0)
            {
                string baseClass = hasBase ? s.DerivedFrom.Name : "TpmStructure";
                string ctorParamList = string.Join(", ", ctorParams.Select(p => $"{CtorParamTypeFor(p)} _{p.Name}"));

                Write($"{className}({ctorParamList})");
                if (hasBase)
                {
                    string ctorParamNamesList = string.Join(", ", ctorParams.Select(p => "_" + p.Name));
                    Write($"  : {baseClass}({ctorParamNamesList})");
                }
                else
                {
                    string memInitList = string.Join(", ", ctorParams.Select(p => p.Type is TpmUnion
                                ? $"{p.Name}(dynamic_cast<{p.Type.Name}*>(_{p.Name}.Clone()))"
                                : $"{p.Name}(_{p.Name})"
                            ));
                    Write($"  : {memInitList}");
                }
                Write("{}");
            }

            if (conversionSource != null)
            {
                Write("");
                Write($"operator ByteVec&() {{ return {conversionSource.Name}; }}");
                Write($"operator const ByteVec&() const {{ return {conversionSource.Name}; }}");
            }

            //
            // Union interface: TpmUnion.GetUnionSelector()
            //
            GenGetUnionSelector(s);

            //
            // Marshaling
            //
            Write("");
            GenMarshalingMethod(true, s, true);
            GenMarshalingMethod(false, s, true);

            string comment = AsSummary("Static marshaling helper");
            WriteComment(comment);
            Write($"static {className} fromTpm(TpmBuffer& buf) {{ return buf.createObj<{className}>(); }}");

            WriteComment(comment);
            Write($"static {className} fromBytes(const ByteVec& buf) {{ return TpmStructure::fromBytes<{className}>(buf); }}");

            //
            // Serialization
            //
            Write("");
            Write($"virtual const char* TypeName () const {{ return \"{className}\"; }}");
            Write("");
            Write("using TpmStructure::Serialize;");
            Write("using TpmStructure::Deserialize;");
            GenMarshalingMethod(true, s, true, true);
            GenMarshalingMethod(false, s, true, true);

            //
            // Cloning and metadata
            //
            Write("");
            Write($"virtual TpmStructure* Clone() const {{ return new {className}(*this); }}");

            var info = s.IsCmdStruct() ? s.Info as CmdStructInfo : null;
            if (info != null && (info.NumHandles != 0 || info.SessEncSizeLen != 0))
            {
                TabOut("");
                TabIn("protected:");

                if (info.NumHandles != 0)
                {
                    Write($"virtual uint16_t numHandles() const {{ return {info.NumHandles}; }}");
                    if (info.IsRequest())
                    {
                        string handles = string.Join(", ", s.Fields.Take(info.NumHandles).Select(f => f.Name));
                        Write($"virtual uint16_t numAuthHandles() const {{ return {info.NumAuthHandles}; }}");
                        Write($"virtual vector<TPM_HANDLE> getHandles() const {{ return {{{handles}}}; }}");
                    }
                    else
                    {
                        Debug.Assert(info.NumHandles == 1 && info.NumAuthHandles == 0);
                        Write($"virtual TPM_HANDLE getHandle() const {{ return {s.Fields[0].Name}; }}");
                        Write($"virtual void setHandle(const TPM_HANDLE& h) {{ {s.Fields[0].Name} = h; }}");
                    }
                }
                if (info.SessEncSizeLen != 0)
                {
                    Debug.Assert(info.SessEncValLen != 0);
                    Write("");
                    Write($"virtual SessEncInfo sessEncInfo() const {{ return {{{info.SessEncSizeLen}, {info.SessEncValLen}}}; }}");
                }
            }

            InsertSnip(s.Name);
            TabOut($"}}; // class {className}");
        } // GenStruct()

        void GenerateTpmCommandPrototypes()
        {
            // Command prototypes
            var commands = TpmTypes.Get<TpmStruct>().Where(s => s.Info.IsRequest());

            TabIn();
            foreach (TpmStruct s in commands)
                GenCommand(s, CommandFlavor.Synch);

            Write("class _DLLEXP_ AsyncMethods");
            Write("{");
            Write("protected: Tpm2& theTpm;");
            Write("public: AsyncMethods(Tpm2& _tpm) : theTpm(_tpm) {}");
            TabIn("public:");
            foreach (TpmStruct s in commands)
                GenCommand(s, CommandFlavor.AsyncCommand);
            foreach (TpmStruct s in commands)
                GenCommand(s, CommandFlavor.AsyncResponse);
            TabOut("};");

            TabOut("public:");
            TabIn();
            Write("AsyncMethods Async;");
            TabOut("};");
            Write("_TPMCPP_END");
        } // GenCommands()

        enum CommandFlavor
        {
            Synch, AsyncCommand, AsyncResponse
        }

        void GenCommand(TpmStruct req, CommandFlavor gen)
        {
            var resp = GetRespStruct(req);
            string cmdName = GetCommandName(req);
            if (gen == CommandFlavor.AsyncResponse)
                cmdName += "Complete";

            string annotation = Helpers.WrapText(AsSummary(req.Comment)) + eol;
            var reqFields = new StructField[0];
            if (gen != CommandFlavor.AsyncResponse)
            {
                reqFields = req.NonTagFields;
                foreach (var f in reqFields)
                    annotation += GetParamComment(f) + eol;
            }
            WriteComment(annotation + (GetReturnComment(resp.NonTagFields)), false);

            string returnType = GetCommandReturnType(gen, resp, cmdName, out string returnFieldName);
            if (reqFields.Length > 1)
            {
                Write(returnType + " " + cmdName);
                TabIn("(");
                if (gen != CommandFlavor.AsyncResponse)
                {
                    foreach (var f in reqFields)
                        Write(CtorParamTypeFor(f) + " " + f.Name + Separator(f, reqFields, ", "));
                }
                TabOut(");");
                Write("");
            }
            else
            {
                string param = reqFields.Length == 1 ? CtorParamTypeFor(reqFields[0]) + " " + reqFields[0].Name : "";
                Write($"{returnType} {cmdName}({param});");
            }
        } // GenCommand()

        void GenerateTpmTypesCpp()
        {
            GenEnumMap();
            GenUnionFactory();
            GenStructsCpp();
            GenCommandDispatchers();
        }

        void GenEnumMap()
        {
            var Enum2Str = new List<string>();
            var Str2Enum = new List<string>();
            string comma = ",";

            foreach (var e in EnumMap)
            {
                string enum2Str = $"{{ typeid({e.Key}).hash_code(), {{",
                       str2Enum = enum2Str;
                foreach (var v in e.Value)
                {
                    string name = '"' + v.Key + '"';
                    enum2Str += " {" + v.Value + "," + name + "},";
                    str2Enum += " {" + name + "," + v.Value + "},";
                }
                if (e.Key == EnumMap.Last().Key)
                    comma = "";
                Enum2Str.Add(enum2Str.TrimEnd(',') + " } }" + comma);
                Str2Enum.Add(str2Enum.TrimEnd(',') + " } }" + comma);
            }

            Write("");
            TabIn("map<size_t, map<uint32_t, string>> Enum2StrMap {");
            foreach (string enumMap in Enum2Str)
                Write(Helpers.WrapText(enumMap, "        "));
            TabOut("};");

            Write("");
            TabIn("map<size_t, map<string, uint32_t>> Str2EnumMap {");
            foreach (string enumMap in Str2Enum)
                Write(Helpers.WrapText(enumMap, "        "));
            TabOut("};");
        } // GenEnumMap()

        void GenUnionFactory()
        {
            var unions = TpmTypes.Get<TpmUnion>();

            WriteComment("Holds static factory method for instantiating TPM unions.\n" +
                "Note: A wrapper class is used instead of simply static function solely " +
                "for the sake of uniformity with languages like C# and Java.");
            Write("struct UnionFactory");
            TabIn("{");
            WriteComment("Creates specific TPM union member based on the union type and selector (tag) value");
            Write("template<class U, typename S>");
            Write("static void Create(shared_ptr<U>& u, S selector) // S = TPM_ALG_ID | TPM_CAP | TPM_ST");
            TabIn("{");
            Write("size_t unionType = typeid(U).hash_code();");

            string elsePref = "";
            foreach (TpmUnion u in unions)
            {
                TabIn($"{elsePref}if (unionType == typeid({u.Name}).hash_code())");
                elsePref = "else ";
                TabIn("switch (selector) {");
                foreach (UnionMember m in u.Members)
                {
                    //if (m.SelectorValue.Name.StartsWith("TPM_ALG_ANY")) continue;
                    string newObj = m.Type.IsElementary() ? "nullptr" : $"new {m.Type.Name}()";
                    Write($"case {m.SelectorValue.QualifiedName}: new (&u) shared_ptr<{u.Name}>({newObj}); return;");
                }
                TabOut("}", false);  // switch (selector)
                TabOut();   // if / else if
            }
            TabIn("else");
            Write("throw runtime_error(\"UnionFactory::Create(): Unknown union type \" + string(typeid(U).name()));");
            TabOut("throw runtime_error(\"Unknown selector value\" + to_string(selector) + \" for union \" + string(typeid(U).name()));");
            TabOut("} // Create()");
            TabOut("}; // class UnionFactory");
        }

        static string GetSerOpCallExprPrefix(StructField f, string op)
        {
            string type = f.Type.StripTypedefs().Name + (f.IsArray() ? "[]" : "");
            string sizeTagParams = f.SizeTagField == null ? ""
                                 : $", \"{f.SizeTagField.Name}\", \"{f.SizeTagField.Type.Name}\"";
            return $"buf.with(\"{f.Name}\", \"{type}\"{sizeTagParams}).{op}";
        }

        public List<string> GetFieldsSerializationOps(StructField[] fields)
        {
            var marshalOps = new List<string>();
            foreach (StructField f in fields)
            {
                int size = f.Type.GetSize();
                string write = GetSerOpCallExprPrefix(f, "write");
                switch (f.MarshalType)
                {
                    case MarshalType.Normal:
                        {
                            if (f.Type.IsElementary())
                                marshalOps.Add($"{write}{WireNameForInt(size)}({f.Name})");
                            else if (f.IsEnum())
                                marshalOps.Add($"{write}Enum({f.Name})");
                            else
                                marshalOps.Add($"{write}Obj({f.Name})");
                            break;
                        }

                    case MarshalType.ConstantValue:
                        marshalOps.Add($"{write}{WireNameForInt(size)}({ConstTag(f)})");
                        break;

                    case MarshalType.SizedStruct:
                        marshalOps.Add($"{write}Obj({f.Name})");
                        break;

                    case MarshalType.EncryptedVariableLengthArray:
                    case MarshalType.SpecialVariableLengthArray:
                        marshalOps.Add($"{write}SizedByteBuf({f.Name})");
                        break;

                    case MarshalType.VariableLengthArray:
                        if (f.IsByteBuffer())
                            marshalOps.Add($"{write}SizedByteBuf({f.Name})");
                        else if (f.IsEnum())
                            marshalOps.Add($"{write}EnumArr({f.Name})");
                        else
                        {
                            Debug.Assert(!f.IsValueType());
                            marshalOps.Add($"{write}ObjArr({f.Name})");
                        }
                        break;

                    case MarshalType.UnionSelector:
                        if ((f.RelatedUnion.Type as TpmUnion).NullSelector == null)
                            marshalOps.Add($"{write}Enum(!{f.RelatedUnion.Name} ? ({f.TypeName})0 : {f.Name}())");
                        else
                            marshalOps.Add($"{write}Enum({f.Name}())");
                        break;

                    case MarshalType.UnionObject:
                        marshalOps.Add($"if ({f.Name}) {write}Obj(*{f.Name})");
                        break;

                    default:
                        Debug.Assert(false);
                        break;
                }
            }
            return marshalOps;
        } // GetFieldsSerializationOps()

        public List<string> GetFieldsDeserializationOps(StructField[] fields)
        {
            var marshalOps = new List<string>();
            foreach (StructField f in fields)
            {
                int size = f.Type.GetSize();
                string read = GetSerOpCallExprPrefix(f, "read");
                string assign = $"{f.Name} = {read}";
                switch (f.MarshalType)
                {
                    case MarshalType.Normal:
                        if (f.Type.IsElementary())
                            marshalOps.Add($"{assign}{WireNameForInt(size)}()");
                        else if (f.IsEnum())
                            marshalOps.Add($"{read}Enum({f.Name})");
                        else
                            marshalOps.Add($"{read}Obj({f.Name})");
                        break;

                    case MarshalType.ConstantValue:
                        // TODO: Add assertion by comparing with the expected constant
                        marshalOps.Add($"{read}{WireNameForInt(size)}()");
                        break;

                    case MarshalType.SizedStruct:
                        marshalOps.Add($"{read}Obj({f.Name})");
                        break;

                    case MarshalType.EncryptedVariableLengthArray:
                    case MarshalType.SpecialVariableLengthArray:
                        marshalOps.Add($"{assign}SizedByteBuf()");
                        break;

                    case MarshalType.VariableLengthArray:
                        if (f.IsByteBuffer())
                            marshalOps.Add($"{assign}SizedByteBuf()");
                        else if (f.IsEnum())
                            marshalOps.Add($"{read}EnumArr({f.Name})");
                        else
                        {
                            Debug.Assert(!f.IsValueType());
                            marshalOps.Add($"{read}ObjArr({f.Name})");
                        }
                        break;

                    case MarshalType.UnionSelector:
                        marshalOps.Add($"{f.TypeName} {f.Name}");
                        marshalOps.Add($"{read}Enum({f.Name})");
                        break;

                    case MarshalType.UnionObject:
                        string selector = (f as UnionField).UnionSelector.Name;
                        marshalOps.Add($"if (!{selector}) {f.Name}.reset()");
                        marshalOps.Add($"else UnionFactory::Create({f.Name}, {selector})");
                        marshalOps.Add($"if ({f.Name}) {read}Obj(*{f.Name})");
                        break;
                    default:
                        break;
                }
            }
            return marshalOps;
        } // GetFieldsDeserializationOps()

        public void GenMarshalingMethod(bool dirTo, TpmStruct s,
                                        bool genPrototype = false, bool serialization = false)
        {
            var fields = serialization ? s.NonSizeFields : s.MarshalFields;
            if (s.DerivedFrom != null || fields.Count() == 0)
            {
                return;
            }

            string dir = serialization ? dirTo ? "Ser" : "Deser"
                                        : dirTo ? "to" : "initFrom",
                    cst = dirTo ? " const" : "",
                    qual = genPrototype ? "" : s.Name + "::",
                    protoAnchor = serialization ? "ialize(Serializer& buf)" : "Tpm(TpmBuffer& buf)",
                    proto = $"void {qual}{dir}{protoAnchor}{cst}";

            if (genPrototype)
            {
                Write($"{proto};");
                return;
            }

            var marshalOps = serialization
                           ? dirTo ? GetFieldsSerializationOps(fields) : GetFieldsDeserializationOps(fields)
                           : dirTo ? GetToTpmFieldsMarshalOps(fields) : GetFromTpmFieldsMarshalOps(fields);

            Write("");
            if (marshalOps.Count == 1)
            {
                // Lay it out in a single line
                Write($"{proto} {{ {marshalOps[0]}; }}");
            }
            else
            {
                Write(proto);
                TabIn("{");
                foreach (var marshalOp in marshalOps)
                    Write($"{marshalOp};");
                TabOut("}");
            }
        } // GenMarshalingMethod()


        // Generates implementation of the TPM structures methods
        void GenStructsCpp()
        {
            foreach (var s in TpmTypes.Get<TpmStruct>())
            {
                if (IsTypedefStruct(s))
                    continue;

                // Marshaling
                GenMarshalingMethod(true, s);
                GenMarshalingMethod(false, s);
                GenMarshalingMethod(true, s, false, true);
                GenMarshalingMethod(false, s, false, true);
            }
        } // GenStructsCpp()

        void GenCommandDispatchers()
        {
            var cmdRequestStructs = TpmTypes.Get<TpmStruct>().Where(s => s.Info.IsRequest());

            foreach (var s in cmdRequestStructs)
                GenCommandDispatcher(s, CommandFlavor.Synch);
            foreach (var s in cmdRequestStructs)
                GenCommandDispatcher(s, CommandFlavor.AsyncCommand);
            foreach (var s in cmdRequestStructs)
                GenCommandDispatcher(s, CommandFlavor.AsyncResponse);
        } // GenCommandDispatchers()


        void GenCommandDispatcher(TpmStruct req, CommandFlavor gen)
        {
            var resp = GetRespStruct(req);
            string cmdName = GetCommandName(req);
            string cmdCode = "TPM_CC::" + cmdName;

            string inStructId = req.Name + "_ID";
            string outStructId = resp.Name + "_ID";

            switch (gen)
            {
                case CommandFlavor.AsyncCommand: cmdName += ""; break;
                case CommandFlavor.AsyncResponse: cmdName += "Complete"; break;
                default: break;
            }

            string returnFieldName;
            string returnType = GetCommandReturnType(gen, resp, cmdName, out returnFieldName);

            string className = " Tpm2::" + (gen != CommandFlavor.Synch ? "AsyncMethods::" : "");
            bool paramsPresent = gen != CommandFlavor.AsyncResponse;
            var cmdParamFields = paramsPresent ? req.NonTagFields : new StructField[0];
            bool multiline = cmdParamFields.Count() > 1;
            StructField lastParm = cmdParamFields.Count() > 0 ? lastParm = cmdParamFields.Last() : null;
            Write(returnType + className + cmdName + "(" +
                    (cmdParamFields.Count() == 0 ? ")"
                  : (multiline ? ""
                  : CtorParamTypeFor(cmdParamFields[0]) + " " + cmdParamFields[0].Name + ")")));
            if (multiline)
            {
                TabIn();
                foreach (var f in cmdParamFields)
                    Write(CtorParamTypeFor(f) + " " + f.Name + (f == lastParm ? ")" : ", "));
                TabOut();
            }
            TabIn("{");

            string reqParams = "";
            if (paramsPresent && req.Fields.Count() != 0)
            {
                string ctorInitList = string.Join(", ", cmdParamFields.Select(f => f.Name));
                Write($"{req.Name} req({ctorInitList});");
                reqParams = ", req";
            }

            string respParam = "";
            if (returnType != "void")
            {
                Debug.Assert(resp.Fields.Count() != 0);
                Write($"{resp.Name} resp;");
                respParam = ", resp";
            }

            string dispatchCall = gen == CommandFlavor.AsyncCommand ? "theTpm.DispatchOut"
                                : gen == CommandFlavor.AsyncResponse ? "theTpm.DispatchIn"
                                : "Dispatch";

            Write(dispatchCall + $"({cmdCode}{reqParams}{respParam});");

            if (gen != CommandFlavor.AsyncCommand)
            {
                if (returnFieldName != null)
                    Write($"return resp.{returnFieldName};");
                else if (returnType != "void")
                    Write("return resp;");

            }
            TabOut("}");
        } // GenCommandDispatcher()

        protected override void WriteComment(string comment, bool wrap = true)
        {
            WriteComment(comment, "/// ", "/// ", "", wrap);
        }
    }
}

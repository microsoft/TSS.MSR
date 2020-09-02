/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Text.RegularExpressions;
using System.Diagnostics;
using System.Collections.Generic;
using System.Linq;

namespace CodeGen
{
    internal class TypeExtractor
    {
        List<RawTable> RawTables;
        HashSet<Domain> ToPostprocess = new HashSet<Domain>();

        const string AlgMacro = "!ALG";
        const string AlgSpecMarker = "!ALG.";
        const string AlgSpecMarkerLC = "!alg.";

        public static Dictionary<string, Tuple<SortedSet<char>, string>> AlgClassifier;

        /// <summary> Data for the class containing constants defined in the TPM 2.0 spec notes
        /// instead of tables, and thus unreachable by the spec document parser. </summary>
        static string[,] ImplementationConstants =
        {
            // The first triplet defines the parameters of the constant collection to be instantiated
            { "ImplementationConstants", "UINT32", "Architecturally defined constants" },

            { "Ossl", "1", "" },
            { "Ltc", "2", "" },
            { "Msbn", "3", "" },
            { "Symcrypt", "4", "" },

            //
            // The following values should be auto-generated based on the set of actually
            // defined algorithms/commands/etc.
            //
            { "HASH_COUNT", "3", "" },
            { "MAX_SYM_KEY_BITS", "256", ""},
            { "MAX_SYM_KEY_BYTES", "((MAX_SYM_KEY_BITS + 7) / 8)", "" },
            { "MAX_SYM_BLOCK_SIZE", "16", "" },
            { "MAX_CAP_CC", "TPM_CC_LAST", "" },
            { "MAX_RSA_KEY_BYTES", "256", "" },
            { "MAX_AES_KEY_BYTES", "32", "" },
            { "MAX_ECC_KEY_BYTES", "48", "" },
            { "ECC_CURVES", "{TPM_ECC_BN_P256, TPM_ECC_NIST_P256, TPM_ECC_NIST_P384}", "UINT16" },

            // LABEL_MAX_BUFFER is the minimum of the largest digest on the device and
            // the largest ECC parameter (MAX_ECC_KEY_BYTES) but no more than 32 bytes.
            { "LABEL_MAX_BUFFER", "32", "" },

            //
            // The following values are defined based on other constants defined in the spec.
            //
            { "_TPM_CAP_SIZE", "sizeof(UINT32)", ""},   // TODO: Workaround for the order of TPM_CAP declaration
            { "MAX_CAP_DATA", "(MAX_CAP_BUFFER-_TPM_CAP_SIZE-sizeof(UINT32))", "" },
            { "MAX_CAP_ALGS", "(MAX_CAP_DATA / sizeof(TPMS_ALG_PROPERTY))", "" },
            { "MAX_CAP_HANDLES", "(MAX_CAP_DATA / sizeof(TPM_HANDLE))", "" },
            { "MAX_TPM_PROPERTIES", "(MAX_CAP_DATA / sizeof(TPMS_TAGGED_PROPERTY))", "" },
            { "MAX_PCR_PROPERTIES", "(MAX_CAP_DATA / sizeof(TPMS_TAGGED_PCR_SELECT))", "" },
            { "MAX_ECC_CURVES", "(MAX_CAP_DATA / sizeof(TPM_ECC_CURVE))", "" },
            { "MAX_TAGGED_POLICIES", "(MAX_CAP_DATA / sizeof(TPMS_TAGGED_POLICY))", "" },

            { "MAX_AC_CAPABILITIES", "(MAX_CAP_DATA / sizeof(TPMS_AC_OUTPUT))", "" },
            { "MAX_ACT_DATA", "MAX_CAP_DATA / sizeof(TPMS_ACT_DATA)", "" }
        };

        // Describes fields of an artificial 'ImplementationData' struct that contains
        // arrays of data describing some aspects of the TPM configuration.
        public class ImplArray
        {
            public string Type;
            public string Name;
            public string Value;

            public ImplArray(string type, string name, string val)
            {
                Type = type;
                Name = name;
                Value = val;
            }
        }

        internal TypeExtractor(List<RawTable> rawTables)
        {
            RawTables = rawTables;
            AlgClassifier = new Dictionary<string, Tuple<SortedSet<char>, string>>();
            TpmTypes.Init();
        }


        static string[] GetAlgsOfType(string typeSpec, string algClass)
        {
            Debug.Assert(typeSpec.Length > 0);
            foreach (char t in typeSpec)
            {
                Debug.Assert(char.IsLower(typeSpec[0]) == char.IsLower(t));
            }

            bool incl = char.IsLower(typeSpec[0]);
            typeSpec = typeSpec.ToUpper();
            var algList = new List<string>();
            foreach (var algType in AlgClassifier)
            {
                var algAttrs = algType.Value.Item1;
                if ((algAttrs.SetEquals(typeSpec) || incl && algAttrs.IsSupersetOf(typeSpec))
                    && (algClass == "" || algType.Value.Item2 == algClass))
                {
                    algList.Add(algType.Key);
                }
            }
            return algList.ToArray();
        }

        static bool IsAlgSpecChar(string text, int pos)
        {
            return pos < text.Length && Char.IsLetter(text[pos]) && text[pos] != '_';
        }

        static void CopyRow(string[,] dstTable, int dstRow, string[,] srcTable, int srcRow)
        {
            Debug.Assert(dstTable.GetLength(1) == srcTable.GetLength(1));
            for (int j = 0; j < srcTable.GetLength(1); ++j)
            {
                dstTable[dstRow, j] = srcTable[srcRow, j];
            }
        }

        static string ExtractAlgSpec(string text)
        {
            int algSpecPos = text.ToUpper().IndexOf(AlgSpecMarker);
            if (algSpecPos == -1)
                return null;

            algSpecPos += AlgSpecMarker.Length - 1;
            string algSpec = "";
            while (IsAlgSpecChar(text, ++algSpecPos))
            {
                algSpec += text[algSpecPos];
            }
            return algSpec;
        }

        static void AlgSpecToAlgMacro(ref string text, string algSpec)
        {
            text = text.Replace(AlgSpecMarkerLC, AlgSpecMarker)
                       .Replace(AlgSpecMarker + algSpec, AlgMacro);
        }

        static string AlgMacroToAlg(string text, string alg)
        {
            return text == AlgMacro ? alg.ToLower() : text.Replace(AlgMacro, alg);
        }

        // Expands tables containing algorithm macros in the caption by creating
        // a duplicate table for each matching algorithm.
        public static RawTable[] ExpandTable(RawTable src, out string algClass)
        {
            Match m;
            string caption = src.TableCaption;
            algClass = "";
            if ((m = Regex.Match(caption, @".*\s*{(?<algClass>\w+)}\s*.*")).Success)
            {
                algClass = m.Groups["algClass"].ToString();
            }

            string algSpec = ExtractAlgSpec(caption);
            if (algSpec == null)
            {
                return null;
            }

            int nRows = src.Table.Length;
            int nCols = src.Table[0].Length;
            for (int j = 0; j < nRows; j++)
            {
                for (int k = 0; k < nCols; k++)
                    AlgSpecToAlgMacro(ref src.Table[j][k], algSpec);
            }
            AlgSpecToAlgMacro(ref src.TableCaption, algSpec);

            string[] algs = GetAlgsOfType(algSpec, algClass);
            var tables = new RawTable[algs.Length];
            for (int i = 0; i < algs.Length; ++i)
            {
                tables[i] = new RawTable(src);
                for (int j = 0; j < nRows; j++)
                {
                    for (int k = 0; k < nCols; k++)
                        tables[i].Table[j][k] = AlgMacroToAlg(src.Table[j][k], algs[i]);
                }
                tables[i].TableCaption = AlgMacroToAlg(src.TableCaption, algs[i]);
                tables[i].Comment = "This table group description: " + src.Comment;
            }
            return tables;
        }

        // Expands algorithm macros in the table.
        public static string[,] ExpandTableRows(string[,] table, string algClass)
        {
            // Map {row index -> expansion alg set}
            var algsMap = new Dictionary<int, string[]>();
            int additionalRows = 0;
            int nRows = table.GetLength(0);
            int nCols = table.GetLength(1);
            for (int i = 1; i < nRows; ++i)
            {
                // Do not check comment column
                for (int j = 0; j < nCols - 1; ++j)
                {
                    string algSpec = ExtractAlgSpec(table[i, j]);
                    if (algSpec != null)
                    {
                        // NewSpecFormat = true;

                        string[] algs = GetAlgsOfType(algSpec, algClass);
                        Debug.Assert(algs.Length > 0);

                        algsMap.Add(i, algs);
                        additionalRows += algs.Length - 1;

                        // Replace algorithm specification with simple algorithm macro
                        // in all cells of this row (they must be identical)
                        for (int k = j; k < nCols; ++k)
                            AlgSpecToAlgMacro(ref table[i, k], algSpec);
                        break;
                    }
                }
            }

            if (algsMap.Count == 0)
            {
                // No algorithm macros in this table.
                // Return the original one unmodified.
                return table;
            }

            var expandedTable = Array.CreateInstance(typeof(string),
                                            nRows + additionalRows, nCols) as string[,];
            int srcRow = 0,
                dstRow = 0;
            // Add an empty end record to streamline the table duplication code
            algsMap.Add(nRows, new string[0]);
            foreach (var algMacroRow in algsMap)
            {
                // Copy (unchanged) rows preceding the next row with an algorithm macro
                for (; srcRow < algMacroRow.Key; ++srcRow, ++dstRow)
                {
                    //CopyRow(expandedTable, dstRow, table, srcRow);
                    for (int j = 0; j < nCols; ++j)
                    {
                        expandedTable[dstRow, j] = table[srcRow, j];
                    }
                }
                // Expand the row with algorithm macros
                foreach (string alg in algMacroRow.Value)
                {
                    Debug.Assert(dstRow < nRows + additionalRows);
                    // Skip the comment column (the last one) for the rows being expanded
                    for (int j = 0; j < nCols - 1; ++j)
                    {
                        expandedTable[dstRow, j] = AlgMacroToAlg(table[srcRow, j], alg);
                    }
                    ++dstRow;
                }
                ++srcRow;
            }
            return expandedTable;
        }


        internal void Extract()
        {
            var skipTables = new string[] { "Definition of Base Types",
                                            "Defines for Architectural Limits Values",
                                            "Defines for Processor Values" };
            RawTable  tblTpmHc = null;
            foreach (RawTable table in RawTables)
            {
                if (Helpers.ContainsOneOf(table.TableCaption, skipTables))
                    continue;

                // Ensure proper ordering of enum definitions for TSS.C++
                if (table.TableCaption.Contains("TPM_HC Constants"))
                {
                    tblTpmHc = table;
                }
                else
                {
                    ProcessTable(table);
                    if (table.TableCaption.Contains("Implementation Values"))
                    {
                        ProcessTable(tblTpmHc);
                    }
                }
            }
            Postprocess();
        }

        internal void Postprocess()
        {
            TpmTypes.InitEnum(ImplementationConstants, "TPM_CC");

            // Expand references to lists of values
            foreach (Domain d in ToPostprocess)
            {
                for (int i = 0; i < d.Count; ++i)
                {
                    if (d[i] is SingleValue)
                    {
                        var constraint = d[i] as SingleValue;
                        string v = constraint.Value.Expr;
                        if (v.StartsWith("$"))
                        {
                            TpmNamedConstant c = TpmTypes.LookupConstant(v.Substring(1));
                            d.Remove(constraint);
                            d.AddConstraints(c.SpecValue.Expr);
                        }
                    }
                }
            }

            // Accumulates information that will be used to generate an artificial
            // 'ImplementationData' struct that contains arrays of data describing
            // some aspects of the TPM configuration.
            var ImplData = new List<ImplArray>();

            // Remove enumerators representing lists of values.
            var toRemove = new List<TpmNamedConstant>();
            foreach (var c in TpmTypes.Constants)
            {
                if (c.SpecValue.Expr.Contains('{'))
                {
                    string type = TpmTypes.Contains(c.Comment) ? c.Comment : "UINT32";
                    ImplData.Add(new ImplArray(type, c.SpecName, c.SpecValue.Expr));
                    toRemove.Add(c);
                }
            }
            foreach (var c in toRemove)
            {
                c.EnclosingEnum.Remove(c);
                if (c.EnclosingEnum.Members.Count == 0)
                {
                    TpmTypes.Remove(c.EnclosingEnum.SpecName);
                }
            }

            var algIds = TpmTypes.Lookup("TPM_ALG_ID") as TpmEnum;
            var algIdVals = new TpmEnum("ALG_ID_VALUE", null, "Proxy constants for TPM_ALG_ID enum");
            algIdVals.UnderlyingType = algIds.UnderlyingType;
            algIdVals.Implement = false;

            foreach (var algId in algIds.Members)
                algIdVals.Add(algId.SpecName.Substring(4, algId.SpecName.Length - 4) + "_VALUE", algId.SpecValue.Expr, algId.Comment);

            TpmTypes.Add(algIdVals, "PLATFORM");

            var e = TpmTypes.Lookup("TPM_RC") as TpmEnum;
            e.Add("TSS_TCP_BAD_HANDSHAKE_RESP", "0x40280001", "Response buffer returned by the TPM is too short");
            e.Add("TSS_TCP_SERVER_TOO_OLD", "0x40280002", "Too old TCP server version");
            e.Add("TSS_TCP_BAD_ACK", "0x40280003", "Bad ack from the TCP end point");
            e.Add("TSS_TCP_BAD_RESP_LEN", "0x40280004", "Wrong length of the response buffer returned by the TPM");
            e.Add("TSS_TCP_UNEXPECTED_STARTUP_RESP", "0x40280005", "TPM2_Startup returned unexpected response code");
            e.Add("TSS_TCP_INVALID_SIZE_TAG", "0x40280006", "Invalid size tag in the TPM response TCP packet");
            e.Add("TSS_TCP_DISCONNECTED", "0x40280007", "TPM over TCP device is not connected");
            e.Add("TSS_DISPATCH_FAILED", "0x40280010", "General TPM command dispatch failure");
            e.Add("TSS_SEND_OP_FAILED", "0x40280011", "Sending data to TPM failed");
            e.Add("TSS_RESP_BUF_TOO_SHORT", "0x40280021", "Response buffer returned by the TPM is too short");
            e.Add("TSS_RESP_BUF_INVALID_SESSION_TAG", "0x40280022", "Invalid tag in the response buffer returned by the TPM");
            e.Add("TSS_RESP_BUF_INVALID_SIZE", "0x40280023", "Inconsistent TPM response parameters size");

            e.Add("TBS_COMMAND_BLOCKED", "0x80280400", "Windows TBS error TPM_E_COMMAND_BLOCKED");
            e.Add("TBS_INVALID_HANDLE", "0x80280401", "Windows TBS error TPM_E_INVALID_HANDLE");
            e.Add("TBS_DUPLICATE_V_HANDLE", "0x80280402", "Windows TBS error TPM_E_DUPLICATE_VHANDLE");
            e.Add("TBS_EMBEDDED_COMMAND_BLOCKED", "0x80280403", "Windows TBS error TPM_E_EMBEDDED_COMMAND_BLOCKED");
            e.Add("TBS_EMBEDDED_COMMAND_UNSUPPORTED", "0x80280404", "Windows TBS error TPM_E_EMBEDDED_COMMAND_UNSUPPORTED");

            e.Add("TBS_UNKNOWN_ERROR", "0x80284000", "Windows TBS returned success but empty response buffer");
            e.Add("TBS_INTERNAL_ERROR", "0x80284001", "Windows TBS error TBS_E_INTERNAL_ERROR");
            e.Add("TBS_BAD_PARAMETER", "0x80284002", "Windows TBS error TBS_E_BAD_PARAMETER");
            e.Add("TBS_INVALID_OUTPUT_POINTER", "0x80284003", "Windows TBS error TBS_E_INVALID_OUTPUT_POINTER");
            e.Add("TBS_INVALID_CONTEXT", "0x80284004", "Windows TBS error TBS_E_INVALID_CONTEXT");
            e.Add("TBS_INSUFFICIENT_BUFFER", "0x80284005", "Windows TBS error TBS_E_INSUFFICIENT_BUFFER");
            e.Add("TBS_IO_ERROR", "0x80284006", "Windows TBS error TBS_E_IOERROR");
            e.Add("TBS_INVALID_CONTEXT_PARAM", "0x80284007", "Windows TBS error TBS_E_INVALID_CONTEXT_PARAM");
            e.Add("TBS_SERVICE_NOT_RUNNING", "0x80284008", "Windows TBS error TBS_E_SERVICE_NOT_RUNNING");
            e.Add("TBS_TOO_MANY_CONTEXTS", "0x80284009", "Windows TBS error TBS_E_TOO_MANY_TBS_CONTEXTS");
            e.Add("TBS_TOO_MANY_RESOURCES", "0x8028400A", "Windows TBS error TBS_E_TOO_MANY_TBS_RESOURCES");
            e.Add("TBS_SERVICE_START_PENDING", "0x8028400B", "Windows TBS error TBS_E_SERVICE_START_PENDING");
            e.Add("TBS_PPI_NOT_SUPPORTED", "0x8028400C", "Windows TBS error TBS_E_PPI_NOT_SUPPORTED");
            e.Add("TBS_COMMAND_CANCELED", "0x8028400D", "Windows TBS error TBS_E_COMMAND_CANCELED");
            e.Add("TBS_BUFFER_TOO_LARGE", "0x8028400E", "Windows TBS error TBS_E_BUFFER_TOO_LARGE");
            e.Add("TBS_TPM_NOT_FOUND", "0x8028400F", "Windows TBS error TBS_E_TPM_NOT_FOUND");
            e.Add("TBS_SERVICE_DISABLED", "0x80284010", "Windows TBS error TBS_E_SERVICE_DISABLED");
            e.Add("TBS_ACCESS_DENIED", "0x80284012", "Windows TBS error TBS_E_ACCESS_DENIED");
            e.Add("TBS_PPI_FUNCTION_NOT_SUPPORTED", "0x80284014", "Windows TBS error TBS_E_PPI_FUNCTION_UNSUPPORTED");
            e.Add("TBS_OWNER_AUTH_NOT_FOUND", "0x80284015", "Windows TBS error TBS_E_OWNERAUTH_NOT_FOUND");


            // Generate aliases for scheme data structures to preserve backward
            // compatibility with pre 1.16 specification revisions
            var newTypes = new List<TpmType>();
            foreach (var t in TpmTypes.TheTypes)
            {
                string origTypeName = t.SpecName;
                if ( t.SpecName.StartsWith("TPMS_") && t.SpecName.Contains("_SCHEME_") && 
                    !t.SpecName.StartsWith("TPMS_SCHEME_") && !t.SpecName.StartsWith("TPMS_NULL_SCHEME_"))
                {
                    string fixedTypeName = "TPMS_SCHEME_" + t.SpecName.Substring(t.SpecName.IndexOf("_SCHEME_") + 8);
                    if (TpmTypes.Contains(fixedTypeName))
                    {
                        Console.WriteLine("Both {0} and {1} are defined", fixedTypeName, t.SpecName);
                    }
                    else
                    {
                        Console.WriteLine("Deriving {0} from {1}", fixedTypeName, t.SpecName);
                        var b = t as TpmStruct;
                        var newStruct = new TpmStruct(fixedTypeName, t.Comment, b);
                        newStruct.InheritMarshalingID = true;
                        newTypes.Add(newStruct);
                    }
                }
            }
            TpmTypes.TheTypes.AddRange(newTypes);

            // Add definitions of additional structures not defined in the TPM 2.0 spec.
            AddTssStructs();
        }

        // Add utility structures exposed and used by the TSS framework.
        public static void AddTssStructs()
        {
            TpmStruct s = new TpmStruct("TssObject", "Contains the public and the plaintext-sensitive and/or encrypted private part of a TPM key (or other object)");
            s.Add(new StructField("TPMT_PUBLIC", "Public", "Public part of key"));
            s.Add(new StructField("TPMT_SENSITIVE", "Sensitive", "Sensitive part of key"));
            s.Add(new StructField("TPM2B_PRIVATE", "Private", "Private part is the encrypted sensitive part of key"));
            TpmTypes.Add(s);
            s = new TpmStruct("PcrValue", "Contains a PCR index and associated hash(pcr-value) [TSS]");
            s.Add(new StructField("UINT32", "index", "PCR Index"));
            s.Add(new StructField("TPMT_HA", "value", "PCR Value"));
            TpmTypes.Add(s);
            s = new TpmStruct("SessionIn", "Structure representing a session block in a command buffer [TSS]");
            s.Add(new StructField("TPM_HANDLE", "handle", "Session handle"));
            s.Add(new StructField("TPM2B_NONCE", "nonceCaller", "Caller nonce"));
            s.Add(new StructField("TPMA_SESSION", "attributes", "Session attributes"));
            s.Add(new StructField("TPM2B_AUTH", "auth", "AuthValue (or HMAC)"));
            TpmTypes.Add(s);
            s = new TpmStruct("SessionOut", "Structure representing a session block in a response buffer [TSS]");
            s.Add(new StructField("TPM2B_NONCE", "nonceTpm", "TPM nonce"));
            s.Add(new StructField("TPMA_SESSION", "attributes", "Session attributes"));
            s.Add(new StructField("TPM2B_AUTH", "auth", "HMAC value"));
            TpmTypes.Add(s);
            s = new TpmStruct("CommandHeader", "Command header [TSS]");
            s.Add(new StructField("TPM_ST", "Tag", "Command tag (sessions, or no sessions)"));
            s.Add(new StructField("UINT32", "CommandSize", "Total command buffer length"));
            s.Add(new StructField("TPM_CC", "CommandCode", "Command code"));
            TpmTypes.Add(s);

            s = new TpmStruct("TSS_KEY", "Contains the public and private part of a TPM key", null, null, true);
            s.Add(new StructField("TPMT_PUBLIC", "publicPart", "Public part of key"));
            //s3.Add(new TpmStructureField("TPMT_SENSITIVE", "sensitivePart", "Sensitive part of key"));
            s.Add(new StructField("TPM2B_PRIVATE_KEY_RSA", "privatePart", "Private part is the encrypted sensitive part of key"));
            TpmTypes.Add(s);

            s = (TpmStruct)TpmTypes.Lookup("TPMT_HA");
            s.Fields[1] = new VariableLengthArray("BYTE", "digest", "Hash value", "hashAlg", s);
            s.Fields[1].MarshalType = MarshalType.SpecialVariableLengthArray;
            // Restore 'Normal' attribute of the first field (changed to ArrayCount by adding the var len array field)
            s.Fields[0].MarshalType = MarshalType.Normal;
        }

        static void CleanInOutAnnotations(ref string str)
        {
            str = str.Replace("<IN>", "[IN]").Replace("<OUT>", "[OUT]")
                     .Replace("<IN/OUT>", "[IN/OUT]").Replace("<IN/OUT >", "[IN/OUT]");
        }

        static string CleanComment(string comment)
        {
            if (string.IsNullOrEmpty(comment))
                return comment;

            comment = char.ToUpper(comment[0]) + comment.Substring(1);

            // NOTE: '<' and '>' are replaced by similarly looking character with different codes to avoid problems in HTML.
            comment = comment.Replace("\r", "").Replace("\t", " ")
                             .Replace(">", "˃").Replace("<", "˂").Replace("&", "∧")
                             .TrimStart(' ').TrimEnd(' ', '\n');
            int len = 0,
                lastLen = comment.Length;
            do {
                lastLen = len;
                comment = comment.Replace("  ", " ");
                len = comment.Length;
            } while (lastLen != len);

            return comment; //.Replace(". ", ".\n");
        }

        bool ProcessTable(RawTable rawTable)
        {
            string algClass;
            RawTable[] expandedTables = ExpandTable(rawTable, out algClass);
            if (expandedTables != null)
            {
                // Table caption contained an algorithm macro.
                // Process the tables resulted from the expansion.
                bool res = true;
                foreach (var tbl in expandedTables)
                {
                    res = res && ProcessTable(tbl);
                }
                return res;
            }

            string[,] table = rawTable.GetTable();
            string caption = rawTable.TableCaption;
            string comment = rawTable.Comment;
            int numHandles = rawTable.NumHandles;

            //CleanTable(table);
            CleanInOutAnnotations(ref comment);
            CleanInOutAnnotations(ref caption);
            comment = CleanComment(comment);

            // Original table rows may be in generic form (i.e. contain algorithm macros)
            table = ExpandTableRows(table, algClass);

            Match m;
            // Alg props -> enum
            if (caption.EndsWith("Defines for Key Size Constants") || 
                (m = Regex.Match(caption, @"Defines for (?<alg>\w+) (?<kind>Symmetric Cipher )?Algorithm Constants")).Success)
            {
                // if (m.Groups["kind"].ToString() != "") NewSpecFormat = true;

                return ProcessConstants(table, "AlgorithmConstants", "UINT16", comment);
            }

            // Constant -> enum
            if ((m = Regex.Match(caption, @"Definition of (\((?<type>\w+)\))?\s*({(?<alg>\w+)}\s*)?(?<name>\w+) Constants")).Success)
            {
                return ProcessConstants(table, m.Groups["name"].ToString(), m.Groups["type"].ToString(), comment);
            }

            // Values -> enum
            if ((m = Regex.Match(caption, @"Defines for (?<name>\w+) (?<kind>\w*)?\s*Values")).Success)
            {
                bool mayContainTypedefs = false;
                string type = "UINT32";
                string name = m.Groups["name"].ToString();
                string kind = m.Groups["kind"].ToString();
                if (kind == "ECC" && rawTable.ContainingSpecPart == SpecPart.AlgRegistry)
                {
                    // Skip ECC curve definition tables.
                    return false;
                }
                if (kind != "Hash")
                {
                    Debug.Assert(kind == "");
                    Debug.Assert(name == "Logic" || name == "Implementation"  || name == "Processor" || name == "PLATFORM");
                    if (name == "Logic")
                        type = "BYTE";
                    else
                        mayContainTypedefs = true;
                }
                return ProcessConstants(table, name, type, comment, mayContainTypedefs);
            }
            
            // Typedefs
            if ((m = Regex.Match(caption, @"Definition of\s*({(?<alg>\w+)}\s*)?Types")).Success)
            {
                return ProcessTypedefs(table, comment);
            }

            // Interface types
            if ((m = Regex.Match(caption, @"Definition of\s*({(?<alg1>\w+)}\s*)?\((?<baseType>\w+)\)\s*({(?<alg2>\w+)}\s*)?(?<restrictedType>\w+)\s+Type")).Success)
            {
                return ProcessInterfaceType(table, m.Groups["restrictedType"].ToString(), m.Groups["baseType"].ToString(), comment);
            }

            // Structures
            if ((m = Regex.Match(caption, @"Definition of\s*(\((?<type>\w+)\)\s*)?({(?<sort>\w+)}\s*)?(?<name>\w+)\s+Structure")).Success)
            {
                // Specifier "type" is a rudiment and is ignored.
                Debug.Assert(numHandles == 0);
                return ProcessStructure(table, m.Groups["name"].ToString(), comment) != null;
            }

            // Bitfields
            if ((m = Regex.Match(caption, @"Definition of\s*\((?<type>\w+)\)\s*(?<name>\w+)\s+Bits")).Success)
            {
                return ProcessBitfieldType(table, m.Groups["name"].ToString(), m.Groups["type"].ToString(), comment);
            }
            // Unions
            if ((m = Regex.Match(caption, @"Definition of\s+(?<name>\w+)\s+Union")).Success)
            {
                return ProcessUnion(table, m.Groups["name"].ToString(), comment);
            }

            if ((m = Regex.Match(caption, @"(?<name>\w+)\s+Command$")).Success)
            {
                return ProcessCommand(table, m.Groups["name"].ToString(), comment, true, numHandles);
            }
            if ((m = Regex.Match(caption, @"(?<name>\w+)\s+Response$")).Success)
            {
                return ProcessCommand(table, m.Groups["name"].ToString(), comment, false, numHandles);
            }

            Console.WriteLine("Failed to match table \"" + caption + "\"");
            return false;
        } // ProcessTable()

        static string GetRowComment(string[,] tbl, int r)
        {
            // Comment is always in the last column of the table;
            return CleanComment(tbl[r, tbl.GetLength(1) - 1]);
        }

        bool ProcessAlgIdTable(TpmEnum coll, string[,] tbl, string enumName, string constType, string enumComment)
        {
            bool firstAdded = false,
                 lastAdded = false;
            TpmNamedConstant lastAlg = null;
            // General form of the check for 'FIRST'/'LAST' members suitable
            // for any table is not used here as we only need to handle one
            // special case of TPM_ALG_ID so far.
            // var enumPrefix = NameTranslator.GetEnumPrefix(enumName);

            // Initialize algorithm classifier
            var definedAlgs = new Dictionary<string, string>();
            for (int r = 1; r < tbl.GetLength(0); r++)
            {
                string  fullName = tbl[r, 0],
                        algID = tbl[r, 1],
                        algTypes = tbl[r, 2],
                        comment = GetRowComment(tbl, r);

                if (!firstAdded)
                {
                    //if (fullName.Substring(enumPrefix.Length) == "FIRST")
                    if (fullName == "TPM_ALG_FIRST")
                    {
                        firstAdded = true;
                    }
                    else if (algTypes != "")
                    {
                        // Inject missing TPM_ALG_FIRST enumerator
                        coll.Add("TPM_ALG_FIRST", algID, comment);
                        firstAdded = true;
                    }
                }

                var newAlg = coll.Add(fullName, algID, comment);

                if (fullName == "TPM_ALG_LAST")
                    lastAdded = true;

                if (algTypes == "" || newAlg == null)
                    continue;

                lastAlg = newAlg;

                Tuple<SortedSet<char>, string> algProps;
                // Drop TPM_ALG_ prefix
                string algName = tbl[r, 0].Substring(8);
                // Check for algorithm synonyms
                string altName;
                if (definedAlgs.TryGetValue(algID, out altName))
                {
                    if (altName.Length >= algName.Length)
                        continue;

                    definedAlgs[algID] = algName;

                    // Remove the classification record for the shorter synonym
                    algProps = AlgClassifier[altName];
                    AlgClassifier.Remove(altName);
                }
                else
                {
                    var algAttrs = new SortedSet<char>();
                    foreach (string algType in algTypes.Split(' '))
                    {
                        if (algType.Length != 1)
                        {
                            Console.WriteLine("Ignoring invalid type classifier {0} for algorithm TPM_ALG_{1}", algType, algName);
                            continue;
                        }
                        algAttrs.Add(algType[0]);
                    }
                    algProps = new Tuple<SortedSet<char>, string>(algAttrs, tbl[r, 3]);
                    definedAlgs.Add(algID, algName);
                }
                AlgClassifier.Add(algName, algProps);
            }

            if (!lastAdded)
            {
                coll.Add("TPM_ALG_LAST", lastAlg.SpecValue.Expr, "");
            }
            coll.Add("TPM_ALG_ANY",  "0x7FFF", "Phony alg ID to be used for the first union member with no selector");
            coll.Add("TPM_ALG_ANY2", "0x7FFE", "Phony alg ID to be used for the second union member with no selector");

            return true;
        } // ProcessAlgIdTable()

        bool ProcessConstants(string[,] tbl, string enumName, string constType,
                              string enumComment, bool mayContainTypedefs = false)
        {
            if (constType == "")
                constType = "UINT32";

            bool alreadyExists = TpmTypes.Contains(enumName);
            TpmEnum coll = alreadyExists
                                ? TpmTypes.Lookup(enumName) as TpmEnum
                                : new TpmEnum(enumName, constType, enumComment);
            if (alreadyExists)
            {
                if (enumName != "AlgorithmConstants")
                {
                    Console.WriteLine("Duplicate table for " + enumName);
                    return false;
                }
            }

            if (enumName == "TPM_ALG_ID")
            {
                ProcessAlgIdTable(coll, tbl, enumName, constType, enumComment);
            }
            else for (int r = 1; r < tbl.GetLength(0); r++)
            {
                string name = tbl[r, 0];
                if (name == "")
                    continue;

                if (name.StartsWith("#"))
                {
                    coll.UnmarshalingError = name.Substring(1);
                    continue;
                }

                string  value = tbl[r, 1],
                        comment = GetRowComment(tbl, r);

                if (mayContainTypedefs && TpmTypes.Contains(value))
                    TpmTypes.Add(new TpmTypedef(name, value, comment));
                else
                    coll.Add(name, value, comment);
            }

            TpmTypes.Add(coll);
            return true;
        } // ProcessConstants()

        bool ProcessTypedefs(string[,] tbl, string comment)
        {
            for (int r = 1; r < tbl.GetLength(0); r++)
            {
                string typeName = tbl[r, 1];
                string underlyingTypeName = tbl[r, 0];

                // If we have a structure typedef we define it properly by cloning the underlying type
                var underlyingType = TpmTypes.Lookup(underlyingTypeName);
                if (underlyingType is TpmStruct)
                    TpmTypes.Add(new TpmStruct(typeName, comment, underlyingType as TpmStruct));
                else
                    TpmTypes.Add(new TpmTypedef(typeName, underlyingTypeName, GetRowComment(tbl, r)));
            }
            return true;
        } // ProcessTypedefs()
        
        bool ProcessInterfaceType(string[,] tbl, string theType, string underlyingType, string comment)
        {
            var t = new TpmTypedef(theType, underlyingType, comment);
            for (int r = 1; r < tbl.GetLength(0); r++)
            {
                string value = tbl[r, 0].Replace(" ", "");
                //string valueComment = tbl[r, 1];
                if (value.StartsWith("#"))
                {
                    // Response code for out-of-domain values
                    t.UnmarshalingError = value.Substring(1);
                    continue;
                }
                if (value.StartsWith("+"))
                {
                    Debug.Assert(t.OptionalValue == null);
                    value = value.Substring(1);
                    Debug.Assert(!value.Contains('{'));
                    t.OptionalValue = value;
                    t.Values.Add(t.OptionalValue);
                    continue;
                }
                if (value.StartsWith("$"))
                {
                    // We can resolve a reference to other constant only after all the tables are processed
                    ToPostprocess.Add(t.Values);
                    Debug.Assert(!value.Contains('{'));
                    t.Values.Add(value);
                    continue;
                }

                Match m = Regex.Match(value, @"^(?<val>\w*)\{?(?<minVal>\w*)?(?<range>\:)?(?<maxVal>\w*)\}?");
                Debug.Assert(m.Success);
                string val = m.Groups["val"].ToString();
                string minVal = m.Groups["minVal"].ToString();
                string maxVal = m.Groups["maxVal"].ToString();
                Debug.Assert(!(val + minVal + maxVal).Contains(','));
                if (m.Groups["range"].ToString() != "")
                {
                    Debug.Assert(val == "" && minVal != "" && maxVal != "");
                    t.Values.Add(minVal, maxVal);
                }
                else
                {
                    Debug.Assert(val != "" && minVal == "" && maxVal == "");
                    t.Values.Add(val);
                }
            }
            TpmTypes.Add(t);
            return true;
        } // ProcessInterfaceType()

        TpmStruct ProcessStructure(string[,] tbl, string theType, string comment, CmdStructInfo info = null)
        {
            TpmStruct newStruct = new TpmStruct(theType, comment, null, info);

            for (int r = 1; r < tbl.GetLength(0); r++)
            {
                string fieldDescr = tbl[r, 0];
                if (fieldDescr == "" || fieldDescr.StartsWith("//"))
                    continue;
                string fieldType = tbl[r, 1];
                if (fieldDescr.StartsWith("#"))
                {
                    // Response code for out-of-domain values
                    newStruct.UnmarshalingError = fieldDescr.Substring(1);
                    continue;
                }

                // Shorten handle field name (as it often contains gratuitous 'object', 'session', etc. prefixes).
                if (fieldDescr.EndsWith("Handle") && theType.EndsWith("Response"))
                    fieldDescr = "handle";

                bool plusStart = fieldType.StartsWith("+");
                bool plusEnd = fieldType.EndsWith("+");
                fieldType = fieldType.Substring(plusStart ? 1 : 0, fieldType.Length - (plusStart || plusEnd ? 1 : 0));

                StructField newField = GetStructureElement(fieldDescr, fieldType, GetRowComment(tbl, r), newStruct);
                if (plusStart || plusEnd)
                    newField.Attrs |= StructFieldAttr.MayBeNull;
                newStruct.Add(newField);
            }
            TpmTypes.Add(newStruct);
            return newStruct;
        } // ProcessStructure()

        void ProcessBitfield (TpmBitfield bf, string bits, string name, string desc)
        {
            Match m;
            if ((m = Regex.Match(bits, @"(?<start>\d+)\:(?<end>\d+)")).Success)
            {
                int startBit = Convert.ToInt32(m.Groups["start"].ToString());
                int endBit = Convert.ToInt32(m.Groups["end"].ToString());
                bf.Add(name, desc, startBit, endBit);
                return;
            }
            if ((m = Regex.Match(bits, @"(?<bitNum>\d+)")).Success)
            {
                int bitIs = Convert.ToInt32(m.Groups["bitNum"].ToString());
                bf.Add(name, desc, bitIs, bitIs);
                return;
            }
            Debug.Assert(false);
        } // ProcessBitfield()

        bool ProcessBitfieldType(string[,] tbl, string collectionName, string baseType, string comment)
        {
            TpmBitfield bf = new TpmBitfield(collectionName, baseType, comment);

            for (int r = 1; r < tbl.GetLength(0); r++)
            {
                if (tbl[r, 0] == "")
                {
                    continue;
                }

                string bits = tbl[r, 0];
                string name = tbl[r, 1];
                string desc = GetRowComment(tbl, r);

                if (name.Contains("/"))
                {
                    name = name.Replace(" ", "");
                    int sepPos = name.IndexOf('/');
                    string firstAlias = name.Substring(0, sepPos);
                    ProcessBitfield(bf, bits, firstAlias, desc);
                    name = name.Substring(sepPos + 1);
                    desc = "Alias to the " + firstAlias + " value.";
                }
                ProcessBitfield(bf, bits, name, desc);
            }
            TpmTypes.Add(bf);
            return true;
        } // ProcessBitfieldType()

        bool ProcessUnion(string[,] tbl, string unionName, string comment)
        {
            TpmUnion u = new TpmUnion(unionName, comment);
            bool any1 = false;
            bool any2 = false;

            if (unionName.IsOneOf("TPMU_NAME", "TPMU_HA", "TPMU_SYM_KEY_BITS", "TPMU_SYM_MODE", "TPMU_ENCRYPTED_SECRET"))
                u.Implement = false;

            for (int r = 1; r < tbl.GetLength(0); r++)
            {
                if (tbl[r, 0] == "")
                    continue;

                string parm = tbl[r, 0];
                string parmType = tbl[r, 1];
                string selector = tbl[r, 2];
                string desc = "";
                if (tbl.GetLength(1) > 3)
                {
                    desc = tbl[r, 3];
                }

                bool plusStart = parmType.StartsWith("+");
                bool plusEnd = parmType.EndsWith("+");
                parmType = parmType.Substring(plusStart ? 1 : 0, parmType.Length - (plusStart || plusEnd ? 1 : 0));

                if (parm.StartsWith("#"))
                    continue;

                if (selector == "")
                {
                    if (!any1)
                    {
                        selector = "TPM_ALG_ANY";
                        any1 = true;
                    }
                    else if (!any2)
                    {
                        selector = "TPM_ALG_ANY2";
                        any2 = true;
                    }
                    else
                    {
                        Debug.Assert("No more than two union fields with no selector are supported now" == null);
                    }
                }

                if (!u.Implement)
                    continue;
                if (parm == "null")
                {
                    u.AllowNull = true;
                    TpmNamedConstant nullValue = TpmTypes.LookupConstant(selector);
                    u.NullSelector = nullValue;
                }

                if (parmType == "")
                {
                    // TODO: see if this branch can be replaced based on the above NullSelector presence

                    // If a selector implies an empty union field we will land here.  In .Net we *derive* the selector from the 
                    // type of the union element so this won't work.  We "solve" this by crafting a new empty structure that can be used
                    string selectorAlg = selector.Substring(selector.LastIndexOf("_") + 1);

                    // Check whether empty base class exist.
                    Debug.Assert(TpmTypes.EmptyUnionBase != null);

                    if (selectorAlg != "NULL" && unionName.Contains("_SCHEME"))
                    {
                        desc =   "Data structure representing an empty " + selectorAlg + " scheme (i.e. the one with \n"
                               + "no parameters to marshal)";
                        parmType = "TPMS_SCHEME_" + selectorAlg;
                    }
                    else
                    {
                        // The name of the data structure is formed by replacing the union name
                        // prefix "TPMU_" with "TPMS_" and injecting selector algorithm name
                        // (like "NULL" or "XOR") immediately after it.
                        desc =   "Custom data structure representing an empty element (i.e. the one with \n"
                                + "no data to marshal) for selector algorithm " + selector;
                        parmType = "TPMS_" + selectorAlg + unionName.Substring(unionName.IndexOf("_"));
                    }
                    desc += " for the union " + unionName;

                    if (!TpmTypes.Contains(parmType))
                    {
                        TpmTypes.Add(new TpmStruct(parmType, desc, TpmTypes.EmptyUnionBase));
                    }
                }
                else if (parm == "")
                {
                    Console.WriteLine("SPEC ERROR? Blank Parameter name in union " + unionName + " definition.");
                    continue;
                }
                u.Add(new UnionMember(parmType, parm, selector, desc));
            }
            TpmTypes.Add(u);
            return true;
        } // ProcessUnion()

        bool ProcessCommand(string[,] tbl, string commandName, string comment,
                            bool request, int numHandles)
        {
            // type and name for command and response are swapped
            for (int j = 0; j < tbl.GetLength(0); j++)
            {
                string tempName = tbl[j, 0];
                tbl[j, 0] = tbl[j, 1];
                tbl[j, 1] = tempName;
            }

            var info = new CmdStructInfo(request, numHandles);

            TpmStruct s = ProcessStructure(tbl, request ? commandName + "_REQUEST"
                                                        : commandName.Substring(5) + "Response",
                                           comment, info);

            // Remove the tag, length, and response code fields from command and response structures
            s.RemoveFields(0, 3);

            if (!request && s.Fields.Count == 0)
            {
                // Do not need a data structure for the command with an empty response.
                s.Implement = false;
                return true;
            }
            return true;
        } // ProcessCommand()

        StructField GetStructureElement(string fieldDescr, string fieldtype, string comment, TpmStruct enclosingStruct)
        {
            Match m;
            string parm = Helpers.RemoveWhitespace(fieldDescr);

            // [select] unionParm
            if ((m = Regex.Match(parm, @"^\[(?<selector>\w+)\](?<name>\w+)")).Success)
            {
                return new UnionField(fieldtype, m.Groups["name"].ToString(), comment,
                                      m.Groups["selector"].ToString(), enclosingStruct);
            }

            if ((m = Regex.Match(parm, @"^(?<name>\w+)\[?(?<countNum>\d*)(?<countTag>\w*)\]?" +
                                       @"\{?(?<minOrOnlyOrListVal>[^\}:=]*)?" +
                                       @"(?<colon>\:)?(?<maxVal>[^\}=]*)\}?(?<eq>\=)?")).Success)
            {
                string name = m.Groups["name"].ToString();
                string countNum = m.Groups["countNum"].ToString();
                string countTag = m.Groups["countTag"].ToString();
                StructField f;

                // No arrays with fixed integer length in the TPM 2.0 spec.
                // Only arrays of fixed size based on the tag value (ALG_ID)
                Debug.Assert(countNum == "");
                if (countTag != "")
                    f = new VariableLengthArray(fieldtype, name, comment, countTag, enclosingStruct);
                else
                    f = new StructField(fieldtype, name, comment);
#if false
                // Unused by the TSS
                if (m.Groups["eq"].ToString() != "")
                {
                    // The remainder of the structure, which consists of the only field referenced
                    // by the ElementToBeSized member, must have exactly the size specified by
                    // this field. Otherwise unmarshaler should return TPM_RC_SIZE.
                    f.ForceSize = true;
                }
#endif
                bool colon = m.Groups["colon"].ToString() != "";
                string val1 = m.Groups["minOrOnlyOrListVal"].ToString();
                if (colon)
                {
                    string maxVal = m.Groups["maxVal"].ToString();
                    TpmConstExpr minValue = null;
                    if (val1 != "")
                        minValue = val1;
                    TpmConstExpr maxValue = null;
                    if (maxVal != "")
                        maxValue = maxVal;
                    f.Domain.Add(minValue, maxValue);
                }
                else if (f.Domain.AddConstraints(val1) == 1)
                {
                    f.MarshalType = MarshalType.ConstantValue;
                }

                var src = f.Domain;
                if (countTag != "" && src.Count == 1)
                {
                    var dst = f.SizeTagField.Domain;
                    if ( dst.Count == 0 )
                    {
                        dst.Add(src[0]);
                    }
                    else
                    {
                        Debug.Assert(dst.Count == 1);
                        UpdateRange(dst, src, Constraint.Type.Min);
                        UpdateRange(dst, src, Constraint.Type.Max);
                        Debug.Assert(f.Domain.Count == 0);
                        f.Domain = dst;
                    }
                }
                return f;
            }

            // needsAuth object
            if (parm.StartsWith("@"))
            {
                StructField f = new StructField(fieldtype, parm.Substring(1), comment);
                f.Attrs |= StructFieldAttr.NeedsAuth;
                (enclosingStruct.Info as CmdStructInfo).NumAuthHandles++;
                return f;
            }

            throw new Exception("Failed to match structure element " + parm);
        }

        void UpdateRange (Domain dst, Domain src, Constraint.Type bnd)
        {
            Debug.Assert(src.Count == 1);
            if (src[0, bnd] == null)
                return;
            Debug.Assert(dst[0, bnd] == null);
            dst[0, bnd] = src[0, bnd];
            src[0, bnd] = null;
            if (src[0].IsEmpty())
            {
                src.Clear();
            }
        }
    }
}

/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Diagnostics;

namespace CodeGen
{

    static class Helpers
    {
        public static bool IsOneOf<T>(this T s, params T[] toCompare)
        {
            foreach (T c in toCompare)
            {
                if (s.Equals(c))
                    return true;
            }
            return false;
        }

        public static bool IsOneOf(this string s, params string[] toCompare)
        {
            foreach (string c in toCompare)
            {
                if (s == c)
                    return true;
            }
            return false;
        }

        public static string Capitalize(string s)
        {
            if (s.Length <= 1) return s;
            if (s.ToUpper() == s)
            {
                // All caps -> just cap the first letter
                s = s.ToLower();
                s = Char.ToUpper(s[0]) + s.Substring(1);
            }
            else
            {
                // Mix -> Just change the first letter
                s = Char.ToUpper(s[0]) + s.Substring(1);
            }
            return s;
        }

        internal static string ToCamelStyle(string s)
        {
            string result = s[0] == '_' ? "_" : "";
            string[] words = s.Split(new[] { '_' });
            for (int j = 0; j < words.Length; j++)
            {
                result += Helpers.Capitalize(words[j]);
            }
            return result;
        }

        internal static bool ContainsOneOf(string s, params string[] l)
        {
            foreach (var x in l)
            {
                if (s.Contains(x))
                    return true;
            }
            return false;
        }

        public static string RemoveWhitespace(string x)
        {
            string res = "";
            foreach (char c in x)
            {
                if (!c.IsOneOf(' ', '\t', '\f', '\n'))
                    res += c;
            }
            return res;
        }

        static bool DanglingWord(string t, int pos, int end = -1)
        {
            if (end == -1)
                end = t.Length;
            return t.IndexOf(' ', pos, end - pos) == -1 && end - pos < (TargetLang.Py ? 8 : 12);
        }

        public static string WrapText(string text, string indent = "")
        {
            int MaxLine = TargetLang.MaxCommentLine;
            int maxLine = MaxLine;
            string t = string.IsNullOrEmpty(indent) ? text
                     : text.Replace("\n", "\n" + indent).Replace("\n" + indent + "\n", "\n\n");
            int pos = 0;
            text = "";
            while (pos + maxLine < t.Length)
            {
                int nlPos = t.IndexOf('\n', pos);
                if (pos < nlPos && (nlPos - pos <= maxLine || DanglingWord(t, pos + maxLine, nlPos)))
                {
                    while (nlPos < t.Length && t[nlPos] == '\n')
                        ++nlPos;
                    if (nlPos == t.Length - 1)
                        break;
                    text += t.Substring(pos, nlPos - pos);
                    pos = nlPos;
                    maxLine = MaxLine;
                    continue;
                }

                int prevPos = pos;
                pos += maxLine;
                if (DanglingWord(t, pos))
                {
                    // Do not leave a signle short word dangling
                    pos -= maxLine;
                    break;
                }
                while (prevPos < pos && t[pos] != ' ' && t[pos] != '\n')
                    --pos;
                if (prevPos == pos)
                {
                    // A very long word (longer than the whole allowed line)

                    //while (pos < t.Length && t[pos] != ' ' && t[pos] != '\n') ++pos;

                    if (pos == t.Length)
                    {
                        pos = prevPos;
                        break;
                    }

                    // Forcibly break the long word
                    pos += maxLine;
                    t = t.Insert(pos, "\n");
                }
                text += t.Substring(prevPos, pos - prevPos) + '\n' + indent;
                ++pos;
                maxLine = MaxLine - indent.Length;
            }
            if (pos < t.Length)
                text += t.Substring(pos);
            Debug.Assert(t.Length <= text.Length);
            return text;
        }
    } // static class Helpers
}

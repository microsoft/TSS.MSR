/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CodeGen
{

    public enum SpecPart
    {
        AlgRegistry = 0,
        VendorSpecific = 1,
        Structures = 2,
        Commands = 3,
    }

    // Basically a string collection representation of the tables in part 2 and part 3.  Typically parsed and then persisted as xml 
    public class RawTable
    {
        public SpecPart ContainingSpecPart;
        public string TableCaption;
        public string Comment;
        public string[][] Table;
        public int NumHandles;

        internal RawTable ()
        {
        }

        internal RawTable (RawTable src)
        {
            ContainingSpecPart = src.ContainingSpecPart;
            TableCaption = src.TableCaption;
            Comment = src.Comment;
            NumHandles = src.NumHandles;

            int nRows = src.Table.Length;
            int nCols = src.Table[0].Length;

            Table = new string[nRows][];
            for (int j = 0; j < nRows; j++)
            {
                Table[j] = new string[nCols];
                for (int k = 0; k < nCols; k++)
                {
                    Table[j][k] = src.Table[j][k];
                }
            }
        }

        internal string[,] GetTable()
        {
            string[,] temp = new string[Table.Length, Table[0].Length];
            for (int j = 0; j < Table.Length; j++)
            {
                for (int k = 0; k < Table[0].Length; k++)
                {
                    temp[j, k] = Table[j][k];
                }
            }
            return temp;
        }
    } // class RawTable

    internal static class RawTables
    {
        internal static List<RawTable> Tables = new List<RawTable>();
        internal static void Add(SpecPart part, string caption, string comment, string[,] contents, int numHandles)
        {
            int len1= contents.GetLength(0);
            int len2= contents.GetLength(1);

            string[][] temp = new string[len1][];
            for (int j = 0; j < len1; j++)
            {
                temp[j] = new string[len2];
                for (int k = 0; k < len2; k++)
                {
                    temp[j][k] = contents[j, k];
                }
            }

            Tables.Add(new RawTable{ContainingSpecPart = part, TableCaption = caption, 
                                    Comment = comment, Table = temp, NumHandles = numHandles});
        }
    }
}

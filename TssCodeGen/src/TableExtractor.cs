/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

 using System;
using System.IO;
using System.Linq;
using Microsoft.Office.Interop.Word;
using System.Diagnostics;

namespace CodeGen
{
    internal class TableExtractor
    {
        internal TableExtractor(string docDirectory)
        {
            string registryDoc = Directory.EnumerateFiles(docDirectory, "*Algorithm Registry*.docx").First();
            string structuresDoc = Directory.EnumerateFiles(docDirectory, "*part 2*.docx").First();         // "*Structures*.docx"
            string commandsDoc = Directory.EnumerateFiles(docDirectory, "*part 3*.docx").First();           // "*Commands*.docx"
            string vendorDoc = Directory.EnumerateFiles(docDirectory, "*Vendor-Specific*.docx").First();
            
            //string pwd = Directory.GetCurrentDirectory();
            //structuresDoc = pwd + Path.DirectorySeparatorChar + structuresDoc;
            //commandsDoc = pwd + Path.DirectorySeparatorChar + commandsDoc;
            ProcessDoc(registryDoc, SpecPart.AlgRegistry);
            ProcessDoc(structuresDoc, SpecPart.Structures);
            ProcessDoc(vendorDoc, SpecPart.VendorSpecific);
            ProcessDoc(commandsDoc, SpecPart.Commands);
        }

        void ProcessDoc(string docName, SpecPart specPart)
        {
            app = new Application();
            //app.Visible = true;
            sourceDoc = app.Documents.Open(docName, Visible: true, ReadOnly: false);
            if (sourceDoc.TrackRevisions)
                sourceDoc.AcceptAllRevisions();

            Paragraph para,
                      prevHeading = null;
            int tableCount = sourceDoc.Tables.Count;
            for (int j = 1; j <= tableCount; j++)
            {
                Table tbl = sourceDoc.Tables[j];
                int rowCount = tbl.Rows.Count;
                int colCount = tbl.Columns.Count;
                Range tableRange = tbl.Range;
                para = tableRange.Paragraphs[1].Previous();
                string tableText = tableRange.Text;
                string tableCaption = para.Range.Text;
                tableCaption = tableCaption.TrimEnd(new char[] { '\r' });
                
                if (!tableCaption.StartsWith("Table") || tableCaption.Contains("xx"))
                {
                    // Skip illustrative tables
                    Console.WriteLine("{0}: Ignoring '{1}'", specPart, tableCaption);
                    continue;
                }

                if (specPart == SpecPart.Commands)
                {
                    // fingerprint for tables
                    if (!(tableCaption.EndsWith("Command") || tableCaption.EndsWith("Response")))
                    {
                        Console.WriteLine("{0}: Skipping '{1}'", specPart, tableCaption);
                        continue;
                    }
                    // Try to extract a description for the command.
                    // Go back to the "General Description" and then get the next paragraph.
                    while (!para.Range.Text.Contains("General Description"))
                    {
                        para = para.Previous();
                    }
                }
                else
                {
                    // fingerprint for tables
                    if (!(tableCaption.Contains("Definition") || tableCaption.Contains("Defines for")))
                    {
                        Console.WriteLine("{0}: Skipping '{1}'", specPart, tableCaption);
                        continue;
                    }

                    // Try to extract a comment for the definition.
                    // Heuristic: Go back to find a heading that looks promising 
                    // and then take the next paragraph.
                    while (!(para.get_Style() as Style).NameLocal.StartsWith("Heading") ||
                           para.Range.Text.StartsWith("Structure Definition"))
                    {
                        para = para.Previous();
                    }
                    if (prevHeading == para)
                    {
                        // Current table does not have a description paragraph.
                        // Use table caption as a comment
                        para = tableRange.Paragraphs[1].Previous().Previous();
                    }
                    else
                    {
                        // Mark the current stop-heading to avoid its reuse for the
                        // next possibly comment-less table.
                        prevHeading = para;
                    }
                }

                int numHandles;
                string[,] stringTable = WordTableToStringTable(tbl, out numHandles, TrimNonPrintable(tableCaption));

                RawTables.Add(specPart, TrimNonPrintable(tableCaption),
                              TrimNonPrintable(para.Next().Range.Text), // comment text
                              stringTable, numHandles);
            }
            ((Microsoft.Office.Interop.Word._Document)sourceDoc).Close(WdSaveOptions.wdDoNotSaveChanges);
            ((Microsoft.Office.Interop.Word._Application)app).Quit(WdSaveOptions.wdDoNotSaveChanges);
        }

        string[,] WordTableToStringTable(Table tbl, out int numHandles, string tableName)
        {
            int rowCount = tbl.Rows.Count;
            int colCount = tbl.Columns.Count;
            // Some tables end with final row that contains comments.  Ignore this row.
            for (int r = 1; r <= rowCount; r++)
            {
                for (int c = 1; c <= colCount; c++)
                {
                    try
                    {
                        string entry = tbl.Cell(r, c).Range.Text;
                    }
                    catch (Exception)
                    {
                        // the column does not exist: stop parsing before this row
                        rowCount = r - 1;
                        break;
                    }
                }
            
            }


            string[,] stringTable = new string[rowCount, colCount];
            int handleStartRow = -1, handleEndRow = -1;
            int lastRow=1;
            for (int r = 1; r <= rowCount; r++)
            {
                // get the border beneath this row.
                Cell rr = tbl.Cell(r, 1);
                Borders bs = rr.Borders;
                Border b = bs[WdBorderType.wdBorderBottom];
                WdLineStyle art = b.LineStyle;

                if (art == WdLineStyle.wdLineStyleDashDotStroked) handleStartRow = r;
                if (art == WdLineStyle.wdLineStyleThinThickThinSmallGap) handleEndRow = r;

                if (!(
                    art == WdLineStyle.wdLineStyleDashDotStroked
                    || art == WdLineStyle.wdLineStyleThinThickThinSmallGap
                    || art == WdLineStyle.wdLineStyleSingle))
                {
                    Debug.WriteLine("Table busted " + tableName + art.ToString());
                }
                
                for (int c = 1; c <= colCount; c++)
                {
                    try
                    {
                        stringTable[r - 1, c - 1] = TrimNonPrintable(tbl.Cell(r, c).Range.Text);
                    }
                    catch (Exception)
                    {
                        Debug.WriteLine("Failed to parse table " + tableName);
                        continue;
                    }
                }
                lastRow = r;
            }
            if (handleStartRow > 0 && handleEndRow < 0)
            {
                handleEndRow = lastRow;
            }
            numHandles = 0;
            if (handleStartRow > 0)
            {
                numHandles = handleEndRow - handleStartRow;
            }
            return stringTable;
        }

        string TrimNonPrintable(string entry)
        {
            char[] trimArray = { ' ', '\r', '\a', '\t' };
            bool  prevSpace = false;
            string outString = "";
            entry = entry.TrimStart(trimArray).TrimEnd(trimArray);
            foreach (char c in entry)
            {
                if (c == '\f')
                    continue;
                if (Char.IsWhiteSpace(c))
                {
                    if (prevSpace)
                        continue;
                    else
                        prevSpace = true;
                }
                else
                    prevSpace = false;
                if (!(Char.IsSymbol(c) || Char.IsLetterOrDigit(c) || Char.IsPunctuation(c) || Char.IsWhiteSpace(c)) ||
                    c < 0x20 && (c != '\r' && c != '\n' && c != '\t') || c > 0x7F)
                {
                    Debug.WriteLine("skipping symbol char" + c + " \n");
                    continue;
                }
                outString += c;

            }
            return outString;

        }

        Object one = 1;
        object missing = System.Reflection.Missing.Value;
        Application app;
        Document sourceDoc;
    }
}

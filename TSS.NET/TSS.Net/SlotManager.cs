/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Diagnostics;
using System.Collections.Generic;
using System.Linq;

/*
 * This file and the associated SlotContext.cs contains three classes that together
 * perform TPM "handle management." TbsContext implements an Tpm2Device interface
 * to Tbs beneath. Typically the programmer will use this as the device underneath
 * a Tpm2. There is one of these per TPM client. Tbs does the actual handle management.  
 * 
 * ObjectContextManager encapsulates the state for TPM clients.
 * 
 */

namespace Tpm2Lib
{
    /// <summary>
    /// Instances of the class TPM are created on top of TPM devices (either a physical
    /// TPM, or another Tbs) via new Tbs(theTpmDevice). TPM device contexts are then
    /// typically created through GetTpm(int locality).
    /// </summary>
    public sealed class Tbs : IDisposable
    {
        private readonly Tpm2Device TpmDevice;
        private readonly Tpm2 Tpm;

        /// <summary>
        /// This parameter supports TPM debugging. Tbs will do a simulated 
        /// S3 StateSave/powerDown/PowerUp/Reload with this probability.
        /// </summary>
        // ReSharper disable once RedundantDefaultFieldInitializer
        private double StateSaveProbability = 0.0;

        /// <summary>
        /// The actual context associated with a TPM client.
        /// </summary>
        private readonly ObjectContextManager ContextManager;

        public Tbs(Tpm2Device theUnderlyingTpm, bool tpmHasRm)
        {
            TpmDevice = theUnderlyingTpm;
            Tpm = new Tpm2(TpmDevice);
            ContextManager = new ObjectContextManager();
            if (!tpmHasRm)
            {
                CleanTpm();
            }
        }

        public Tpm2 GetUnderlyingTpm()
        {
            return Tpm;
        }

        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Reliability", "CA2000:Dispose objects before losing scope")]
        public TbsContext CreateTbsContext()
        {
            lock (this)
            {
                TbsContext newContext = null;
                try
                {
                    newContext = new TbsContext(this);
                    return newContext;
                }
                catch (Exception)
                {
                    if (newContext != null)
                    {
                        newContext.Dispose();
                    }
                    return null;
                }

            }
        }

        public void DestroyContext(Tpm2Device tpmDevice)
        {
            throw new NotImplementedException("");
        }

        public int NumEntitiesInTpm()
        {
            int num1 = GetLoadedEntities(Tpm, Ht.Transient).Length;
            int num2 = GetLoadedEntities(Tpm, Ht.LoadedSession).Length;
            int num3 = GetLoadedEntities(Tpm, TpmHelpers.GetEnumerator<Ht>("ActiveSession", "SavedSession")).Length;
            return num1 + num2 + num3;
        }

        public int NumActiveContexts()
        {
            return ContextManager.ObjectContexts.Count;
        }

        /// <summary>
        /// If probability is not 0.0, the SlotManager will randomly cycle the TPM
        /// through a simulated S3 transition at the start of DispatchCommand
        /// (before the requested command is invoked).
        /// </summary>
        /// <param name="probability"></param>
        public void SetS3Probability(double probability)
        {
            StateSaveProbability = probability;
        }

        // Debug support
        private int CommandNumber;
        // ReSharper disable once NotAccessedField.Local
        private int LastStateSaveCommandNumber;

        /// <summary>
        /// Dispatch a command to the underlying TPM. This method implements all
        /// significant functionality. It examines the command stream and performs
        /// (approximately) the following actions:
        /// 1) If the command references a handle (session or transient object), then
        ///     TBS makes sure that the entity  is loaded. If it is, then the handle is
        ///     "translated" to the underlying TPM handle. If it is not, then TBS checks
        ///     to see if it has a saved context for the entity, and if so, loads it.
        /// 2) If the command will fill a slot, then TBS ensures that a slot is available.
        ///     It does this by ContextSaving the LRU entity of the proper type (that is
        ///     not used in this command).
        /// </summary>
        /// <param name="caller"></param>
        /// <param name="active"></param>
        /// <param name="inBuf"></param>
        /// <param name="outBuf"></param>
        /// <exception cref="Exception"></exception>
        internal void DispatchCommand(TbsContext caller, CommandModifier active, byte[] inBuf, out byte[] outBuf)
        {
            lock (this)
            {
                CommandNumber++;
                // ReSharper disable once CompareOfFloatsByEqualityOperator
                if (StateSaveProbability != 0.0)
                {
                    // S3 debug support
                    DebugStateSave();
                    LastStateSaveCommandNumber = CommandNumber;
                }

                CommandHeader commandHeader;
                TpmHandle[] inHandles;
                SessionIn[] inSessions;
                byte[] commandParmsNoHandles;
                bool legalCommand = CommandProcessor.CrackCommand(inBuf,
                        out commandHeader, out inHandles, out inSessions, out commandParmsNoHandles);

                if (!legalCommand)
                {
                    // Is a diagnostics command.  Pass through to TPM (a real RM would refuse).
                    TpmDevice.DispatchCommand(active, inBuf, out outBuf);
                    return;
                }

                TpmCc cc = commandHeader.CommandCode;

                // Lookup command
                CommandInfo command = Tpm2.CommandInfoFromCommandCode(cc);
                if (command == null)
                {
                    throw new Exception("Unrecognized command");
                }

                if (cc == TpmCc.ContextLoad || cc == TpmCc.ContextSave)
                {
                    Debug.WriteLine("ContextLoad and ContextSave are not supported in this build");
                    outBuf = Marshaller.GetTpmRepresentation(new Object[] {
                        TpmSt.NoSessions,
                        (uint)10,
                        TpmRc.NotUsed });
                }

                // Look up referenced objects and sessions
                ObjectContext[] neededObjects = GetReferencedObjects(caller, inHandles);
                ObjectContext[] neededSessions = GetSessions(caller, inSessions);
                ObjectContext[] neededEntities =
                        neededObjects != null
                            ? neededSessions != null
                                ? neededObjects.Concat(neededSessions).ToArray()
                                : neededObjects
                            : neededSessions;
#if false
                // Tpm2Tester may intentionally use invalid handles, therefore it always
                // work in the passthru mode (all correctness checks by TSS infra suppressed)
                if (!Tpm2._TssBehavior.Passthrough &&
                    (neededObjects == null || neededSessions == null))
#endif
                if (neededObjects == null || neededSessions == null)
                {
                    // One or more of the handles was not registered for the context
                    byte[] ret = FormatError(TpmRc.Handle);
                    outBuf = ret;
                    return;
                }

                // Load referenced objects and sessions (free slots if needed)
                // It's important to load all object and session handles in a single call
                // to LoadEntities(), as for some commands (e.g. GetSessionAuditDigest)
                // the objects array may contain session handles. In this case the session
                // handles loaded by the invocation of LoadEntities for neededObjects
                // may be evicted again during the subsequent call for neededSessions.
                var expectedResponses = Tpm._GetExpectedResponses();
                if (!LoadEntities(neededEntities))
                {
                    throw new Exception("Failed to make space for objects or sessions");
                }
                else
                {
                    // At this point everything referenced should be loaded, and
                    // there will be a free slot if needed so we can translate
                    // the input handles to the underlying handles 
                    ReplaceHandlesIn(inHandles, inSessions, neededObjects, neededSessions);
                }

                // Re-create the command using translated object and session handles
                byte[] commandBuf = CommandProcessor.CreateCommand(commandHeader.CommandCode,
                                                inHandles, inSessions, commandParmsNoHandles);
                if (!Tpm2._TssBehavior.Passthrough)
                    Debug.Assert(commandBuf.Length == inBuf.Length);

                byte[] responseBuf;
                
                // TODO: Virtualize TPM2_GetCapability() for handle enumeration.

                //
                // Execute command on underlying TPM device.
                // If we get an ObjectMemory or SessionMemory error we try to make more space and try again
                // Note: If the TPM device throws an error above we let it propagate out.  There should be no side 
                // effects on TPM state that the TBS cares about.
                //
                ulong firstCtxSeqNum = 0;
                while (true)
                {
                    Tpm._ExpectResponses(expectedResponses);
                    TpmDevice.DispatchCommand(active, commandBuf, out responseBuf);

                    TpmRc res = GetResultCode(responseBuf);
                    if (res == TpmRc.Success ||
                        expectedResponses != null && expectedResponses.Contains(res))
                    {
                        break;
                    }

                    if (res == TpmRc.ContextGap)
                    {
                        ulong seqNum = ShortenSessionContextGap(firstCtxSeqNum);
                        if (seqNum == 0)
                            break;  // Failed to handle CONTEXT_GAP error

                        if (firstCtxSeqNum == 0)
                            firstCtxSeqNum = seqNum;
                        
                        //if (firstCtxSeqNum != 0)
                        //    Console.WriteLine("DispatchCommand: CONTEXT_GAP handled");
                        continue;
                    }

                    var slotType = SlotType.NoSlot;
                    if (res == TpmRc.ObjectHandles || res == TpmRc.ObjectMemory)
                    {
                        slotType = SlotType.ObjectSlot;
                    }
                    else if (res == TpmRc.SessionHandles || res == TpmRc.SessionMemory)
                    {
                        slotType = SlotType.SessionSlot;
                    }
                    else
                    {
                        // Command failure not related to resources
                        break;
                    }
                    if (!MakeSpace(slotType, neededEntities))
                    {
                        // Failed to make an object slot in the TPM
                        responseBuf = TpmErrorHelpers.BuildErrorResponseBuffer(TpmRc.Memory);
                        break;
                    }
                }

                // Parse the response from the TPM
                TpmSt responseTag;
                uint responseParamSize;
                TpmRc resultCode;
                TpmHandle[] responseHandles;
                SessionOut[] responseSessions;
                byte[] responseParmsNoHandles, responseParmsWithHandles;

                CommandProcessor.SplitResponse(responseBuf,
                                               command.HandleCountOut,
                                               out responseTag,
                                               out responseParamSize,
                                               out resultCode,
                                               out responseHandles,
                                               out responseSessions,
                                               out responseParmsNoHandles,
                                               out responseParmsWithHandles);

                // In case of an error there is no impact on the loaded sessions, but
                // we update the LRU values because the user will likely try again.
                if (resultCode != TpmRc.Success)
                {
                    outBuf = responseBuf;
                    UpdateLastUseCount(new[] {neededObjects, neededSessions});
                    return;
                }

                // Update TBS database with any newly created TPM objects
                ProcessUpdatedTpmState(caller, command, responseHandles, neededObjects);

                // And if there were any newly created objects use the new DB entries
                // to translate the handles
                ReplaceHandlesOut(responseHandles);
                outBuf = CommandProcessor.CreateResponse(resultCode, responseHandles,
                                                responseSessions, responseParmsNoHandles);

                Debug.Assert(outBuf.Length == responseBuf.Length);
            } // lock(this)
        }

        /// <summary>
        /// Updates TBS context database for commands that either fill or empty slots.
        /// </summary>
        /// <param name="caller"></param>
        /// <param name="command"></param>
        /// <param name="responseHandles"></param>
        /// <param name="inputObjects"></param>
        private void ProcessUpdatedTpmState(TbsContext caller, CommandInfo command,
                                            TpmHandle[] responseHandles,
                                            ObjectContext[] inputObjects)
        {
            switch (command.CommandCode)
            {
                // Commands that fill a slot (apart from contextLoad, which is more complex)
                case TpmCc.Load:
                case TpmCc.LoadExternal:
                case TpmCc.CreatePrimary:
                case TpmCc.CreateLoaded:
                case TpmCc.MacStart:
                case TpmCc.HashSequenceStart:
                case TpmCc.StartAuthSession:
                {
                    var t = new TpmHandle(responseHandles[0].handle);
                    // ReSharper disable once UnusedVariable
                    ObjectContext context2 = ContextManager.CreateObjectContext(caller, t);
                    break;
                }
                case TpmCc.ContextLoad:
                case TpmCc.ContextSave:
                {
                    throw new Exception("ProcessUpdatedTpmState: Should not be here");
                }
                case TpmCc.FlushContext:
                case TpmCc.SequenceComplete:
                {
                    if (inputObjects != null)
                        ContextManager.Remove(inputObjects[0]);
                    break;
                }
                case TpmCc.EventSequenceComplete:
                {
                    if (inputObjects != null)
                        ContextManager.Remove(inputObjects[1]);
                    break;
                }
                case TpmCc.Clear:
                {
                    ProcessTpmClear(caller, Tbs.SlotType.SessionSlot);
                    break;
                }
            }
        }

        void ProcessTpmClear(TbsContext caller, Tbs.SlotType excluded = Tbs.SlotType.NoSlot)
        {
            ContextManager.ObjectContexts.RemoveAll(ctx => ctx.Owner == caller &&
                                                           ctx.TheSlotType != excluded);
            Debug.Assert(excluded != Tbs.SlotType.NoSlot || ContextManager.ObjectContexts.Count == 0);
        }

        /// <summary>
        /// Perform a StateSave/StateReload depending on a coin flip
        /// </summary>
        private int NumStateSaves;

        private void DebugStateSave()
        {
            if (Globs.GetRandomDouble() < StateSaveProbability)
            {
                bool s3 = NumStateSaves % 4 < 2;
                bool doPowerCycle = (NumStateSaves % 2 == 0);
                string message = s3 ? "{S3}" : "{S4}";
                if (!doPowerCycle)
                {
                    message = "{S3-abort}";
                }
#if WINDOWS_UWP
                Debug.WriteLine(message);
#else
                Console.ForegroundColor = ConsoleColor.Magenta;
                Console.Error.Write(message);
                Console.ResetColor();
#endif
                StateSaveAndReload(s3, (NumStateSaves % 2 == 0));
                NumStateSaves++;
            }
        }

        private TpmRc GetResultCode(byte[] responseBuf)
        {
            var mOut = new Marshaller(responseBuf);
            // ReSharper disable once UnusedVariable
            var responseTag = mOut.Get<TpmSt>();
            // ReSharper disable once UnusedVariable
            var responseParamSize = mOut.Get<uint>();
            var resultCode = mOut.Get<TpmRc>();
            return resultCode;
        }

        /// <summary>
        /// TPM Debug support. Cycle the TPM through:
        /// (a) SaveContext all loaded contexts;
        /// (b) StateSave();
        /// (c) powerOff;
        /// (d) powerOn;
        /// (e) Startup(SU_State) or Startup(S_CLEAR).
        /// Then needed objects and sessions will be paged back in as needed.
        /// NOTE 1. This helper is NOT thread safe.
        /// NOTE 2. This helper has side-effects on the startup counter and 
        /// will likely result in clock discontinuities.
        /// </summary>
        private void StateSaveAndReload(bool startupState, bool doPowerCycle)
        {
            CheckConsistency();
            ContextSaveEverything();
            CheckConsistency();
            // Record TPM state so that we can check it's the same through shutdown.
            // The exception is entities that are marked volatileLoad which are destroyed on S4.
            TpmHandle[] startHandles = GetAllLoadedEntities(Tpm);

            Tpm.Shutdown(Su.State);
            // Optionally cycle the power (if the power is not cycled the TPM should be able to carry on as if nothing had happened.
            string stateTransition = "none";

            if (doPowerCycle)
            {
                TpmDevice.PowerCycle();
                // Re-init through simulated S3 and S4 alternately
                // ReSharper disable once RedundantAssignment
                stateTransition = "";
                if (startupState)
                {
                    stateTransition = "S3";
                    Tpm.Startup(Su.State);
                }
                else
                {
                    stateTransition = "S4";
                    Tpm.Startup(Su.Clear);
                }
            }

            // Check everything is the same
            TpmHandle[] endHandles = GetAllLoadedEntities(Tpm);
            bool match = true;
            if (startHandles.Length == endHandles.Length)
            {
                for (int j = 0; j < startHandles.Length; j++)
                {
                    if (startHandles[j].handle != endHandles[j].handle)
                    {
                        match = false;
                    }
                }
            }
            else
            {
                match = false;
            }
            if (!match)
            {
                string message = "Handle set did not survive " + stateTransition;
                throw new Exception(message);
            }
        }

        private void ContextSaveEverything()
        {
            // SaveContext all loaded contexts
            foreach (ObjectContext c in ContextManager.ObjectContexts)
            {
                // See if this context is a candidate for eviction
                if (!c.Loaded)
                {
                    continue;
                }
                c.Context = Tpm.ContextSave(c.TheTpmHandle);
                // Object slots are not auto-evicted on ContextSave
                if (SlotTypeFromHandle(c.Context.savedHandle) == SlotType.ObjectSlot)
                {
                    Tpm.FlushContext(c.TheTpmHandle);

                }
                c.TheTpmHandle = null;
                c.Loaded = false;
            }
        }

        /// <summary>
        /// Get the TBS ObjectContext given SessionIn objects collected from the inputs stream.
        /// </summary>
        /// <param name="caller"></param>
        /// <param name="inSessions"></param>
        /// <returns></returns>
        private ObjectContext[] GetSessions(TbsContext caller, SessionIn[] inSessions)
        {
            var contexts = new ObjectContext[inSessions.Length];
            for (int j = 0; j < inSessions.Length; j++)
            {
                contexts[j] = ContextManager.GetContext(caller, inSessions[j].handle);
                if (contexts[j] == null)
                {
                    return null;
                }
            }
            return contexts;
        }

        public void DebugAssertTpmIsEmpty()
        {
            Debug.Assert(ContextManager.ObjectContexts.Count == 0);
            CheckConsistency();
        }

        /// <summary>
        /// Create a 10 byte error response that matches TPM error responses.
        /// </summary>
        /// <param name="errorCode"></param>
        /// <returns></returns>
        private byte[] FormatError(TpmRc errorCode)
        {
            var m = new Marshaller();
            m.Put(TpmSt.NoSessions, "");
            m.Put((uint)10, "");
            m.Put(errorCode, "");
            return m.GetBytes();
        }

        /// <summary>
        /// Removes all TPM objects referenced by this context and then removes
        /// the context from the TBS database.
        /// </summary>
        /// <param name="c"></param>
        public void DisposeContext(TbsContext c)
        {
            lock (this)
            {
                foreach (ObjectContext o in ContextManager.ObjectContexts)
                {
                    if (o.Owner != c)
                        continue;
                    if (o.Loaded)
                    {
                        Tpm._AllowErrors()
                           .FlushContext(o.TheTpmHandle);
                        if (!Tpm._LastCommandSucceeded())
                        {
                            Debug.WriteLine("TRM failed to flush a handle: {0:X8}", o.TheTpmHandle);
                        }
                        o.Loaded = false;
                    }
                }
                ContextManager.RemoveAll(c);
            }
        }

        public void Dispose()
        {
            lock (this)
            {
                foreach (ObjectContext o in ContextManager.ObjectContexts)
                {
                    if (o.Loaded)
                    {
                        Tpm.FlushContext(o.TheTpmHandle);
                    }
                }
                ContextManager.RemoveAll();
            }
            Tpm.Dispose();
        }

        private void CleanTpm()
        {
            var loaded = new TpmHandle[3][];
            loaded[0] = GetLoadedEntities(Tpm, Ht.Transient);
            loaded[1] = GetLoadedEntities(Tpm, Ht.LoadedSession);
            loaded[2] = GetLoadedEntities(Tpm, TpmHelpers.GetEnumerator<Ht>("ActiveSession", "SavedSession"));
            foreach (TpmHandle[] arr in loaded)
            {
                foreach (TpmHandle h in arr)
                {
                    Tpm.FlushContext(h);
                }
            }
        }

        /// <summary>
        /// Look up TBS ObjectContext records given the handles in the inHandles input parms.
        /// </summary>
        /// <param name="caller"></param>
        /// <param name="inHandles"></param>
        /// <returns></returns>
        private ObjectContext[] GetReferencedObjects(TbsContext caller, TpmHandle[] inHandles)
        {
            var neededContexts = new ObjectContext[inHandles.Length];
            for (int j = 0; j < inHandles.Length; j++)
            {
                neededContexts[j] = ContextManager.GetContext(caller, inHandles[j]);
                if (neededContexts[j] == null)
                {
                    return null;
                }
            }
            return neededContexts;
        }

        /// <summary>
        /// Ensure that all referenced objects are loaded.
        /// </summary>
        /// <param name="neededContexts"></param>
        private bool LoadEntities(ObjectContext[] neededContexts)
        {
            return (from t in neededContexts where !t.Loaded
                    select LoadObject(t, neededContexts))
                    .All(loaded => loaded);
        }

        /// <summary>
        /// Load an object making a space if needed. If we need to make a space then we are
        /// mindful not to evict anything in the doNotEvict array.
        /// </summary>
        /// <param name="contextToLoad"></param>
        /// <param name="doNotEvict"></param>
        private bool LoadObject(ObjectContext contextToLoad, ObjectContext[] doNotEvict)
        {
            do
            {
                contextToLoad.TheTpmHandle = Tpm._AllowErrors()
                                                .ContextLoad(contextToLoad.Context);
                if (Tpm._LastCommandSucceeded())
                    break;
                if (!MakeSpace(contextToLoad.TheSlotType, doNotEvict))
                    return false;
            } while (true);
            contextToLoad.Loaded = true;
            return true;
        }

        /// <summary>
        /// Finds the oldest saved session, re-loads and re-saves it to shorten the session context gap.
        /// </summary>
        /// <param name="firstCtxSeqNum"></param>
        ulong ShortenSessionContextGap(ulong firstCtxSeqNum)
        {
            var ctx = ContextManager.GetOldestSavedSession();
            if (ctx == null || ctx.Context.sequence == firstCtxSeqNum)
            {
                Debug.WriteLine("FAILED to FIND sess ctx to re-save: {0}", ctx);
                return 0;
            }
            ctx.TheTpmHandle = Tpm._AllowErrors()
                                    .ContextLoad(ctx.Context);
            if (!Tpm._LastCommandSucceeded())
            {
                Debug.WriteLine("FAILED for RE-LOAD sess ctx to re-save: {0}", Tpm._GetLastResponseCode());
                return 0;
            }

            ctx.Context = Tpm._AllowErrors()
                                .ContextSave(ctx.TheTpmHandle);
            if (!Tpm._LastCommandSucceeded())
            {
                Debug.WriteLine("FAILED for RE-SAVE re-loaded sess ctx: {0}", Tpm._GetLastResponseCode());
                ctx.Loaded = true;
                return 0;
            }
            return ctx.Context.sequence;
        }

        /// <summary>
        /// Make a space in the TPM for an entity of type neededSlot, keeping pinned
        /// entities (the ones used by the current command) loaded.
        /// </summary>
        /// <param name="type"></param>
        /// <param name="pinnedEntities"></param>
        private bool MakeSpace(SlotType type, ObjectContext[] pinnedEntities)
        {
            ObjectContext entityToEvict = ContextManager.GetEntityToEvict(type, pinnedEntities);
            if (entityToEvict == null)
                return false;

            // Candidate is the entity that we need to context-save.
            // TODO: Handle possible TPM_RC_CONTEXT_GAP error
            ulong firstCtxSeqNum = 0;
            Context savedCtx = null;
            while (true)
            {
                savedCtx = Tpm._AllowErrors()
                              .ContextSave(entityToEvict.TheTpmHandle);

                if (Tpm._LastCommandSucceeded())
                    break;

                if (Tpm._GetLastResponseCode() != TpmRc.ContextGap)
                {
                    Debug.WriteLine("MakeSpace: ContextSave FAILED: {0}", Tpm._GetLastResponseCode());
                    return false;
                }

                ulong seqNum = ShortenSessionContextGap(firstCtxSeqNum);
                if (seqNum == 0)
                    return false;
                if (firstCtxSeqNum == 0)
                    firstCtxSeqNum = seqNum;
            }
            //if (firstCtxSeqNum != 0)
            //    Console.WriteLine("MakeSpace: CONTEXT_GAP handled");

            // Update our internal database
            entityToEvict.Context = savedCtx;

            // Non-session transient objects need to be removed from TPM after saving.
            // TODO: Manage the saved-context array.
            if (type != SlotType.SessionSlot)
            {
                Tpm.FlushContext(entityToEvict.TheTpmHandle);
            }
            entityToEvict.TheTpmHandle = null;
            entityToEvict.Loaded = false;
            return true;
        }

        /// <summary>
        /// Modifies the handles and sessions arrays so that they contain the translated handles.
        /// </summary>
        /// <param name="objects"></param>
        /// <param name="sessions"></param>
        /// <param name="objCtx"></param>
        /// <param name="sessCtx"></param>
        private void ReplaceHandlesIn(TpmHandle[] objects, SessionIn[] sessions,
                                      ObjectContext[] objCtx, ObjectContext[] sessCtx)
        {
            if (objCtx != null)
            {
                for (int j = 0; j < objects.Length; j++)
                    objects[j] = objCtx[j].TheTpmHandle;
            }
            if (sessCtx != null)
            {
                for (int j = 0; j < sessions.Length; j++)
                    sessions[j].handle = sessCtx[j].TheTpmHandle;
            }
        }

        private void ReplaceHandlesOut(IEnumerable<TpmHandle> outHandles)
        {
            foreach (TpmHandle t in outHandles)
            {
                t.handle = ContextManager.TbsHandleFromTpmHandle(t.handle);
            }
        }

        private void UpdateLastUseCount(IEnumerable<ObjectContext[]> entities)
        {
            foreach (ObjectContext[] arr in entities)
            {
                if (arr == null)
                    continue;

                foreach (ObjectContext c in arr)
                {
                    c.LastUseCount = ContextManager.GetUseCount();
                }
            }
        }

        internal static SlotType SlotTypeFromHandle(TpmHandle h)
        {
            switch (h.GetType())
            {
                case Ht.Transient:
                    return SlotType.ObjectSlot;
                case Ht.PolicySession:
                case Ht.HmacSession:
                    return SlotType.SessionSlot;
                default:
                    return SlotType.NoSlot;
            }
        }

        public int GetSessionCount(out int savedSessionCount)
        {
            int numSavedSession = 0;
            // ReSharper disable once NotAccessedVariable
            int numLoadedSession = 0;
            foreach (ObjectContext o in ContextManager.ObjectContexts)
            {
                if (o.TheSlotType == SlotType.SessionSlot)
                {
                    if (o.Loaded)
                    {
                        numLoadedSession++;
                    }
                    else
                    {
                        numSavedSession++;
                    }
                }
            }

            savedSessionCount = numSavedSession;
            return numSavedSession;
        }

        public int GetFreeSessionCount()
        {
            return ContextManager.NumFreeSlots(SlotType.SessionSlot);
        }

        private TpmHandle[] GetLoadedEntities(Tpm2 tpm, Ht rangeToQuery)
        {
            const uint maxHandles = UInt32.MaxValue;
            ICapabilitiesUnion h;
            byte moreData = tpm.GetCapability(Cap.Handles, ((uint)rangeToQuery) << 24, maxHandles, out h);
            if (moreData != 0)
            {
                throw new NotImplementedException("Too much data returned");
            }
            if (h.GetType() != typeof (HandleArray))
            {
                throw new Exception("Incorrect type");
            }
            var handles = (HandleArray)h;
            return handles.handle;
        }

        private TpmHandle[] GetAllLoadedEntities(Tpm2 tpm)
        {
            var handles = new List<TpmHandle>();
            handles.AddRange(GetLoadedEntities(tpm, Ht.Transient));
            handles.AddRange(GetLoadedEntities(tpm, Ht.LoadedSession));
            handles.AddRange(GetLoadedEntities(tpm, TpmHelpers.GetEnumerator<Ht>("ActiveSession", "SavedSession")));
            return handles.ToArray();
        }

        private void CheckConsistency(string message = "")
        {
            TpmHandle[] loadedObjects = GetLoadedEntities(Tpm, Ht.Transient);
            TpmHandle[] loadedSessions = GetLoadedEntities(Tpm, Ht.LoadedSession);
            TpmHandle[] contextSavedSessions = GetLoadedEntities(Tpm, TpmHelpers.GetEnumerator<Ht>("ActiveSession", "SavedSession"));

            int numLoadedObject = 0, numLoadedSession = 0, numSavedSession = 0;
            foreach (ObjectContext o in ContextManager.ObjectContexts)
            {
                if (o.TheSlotType == SlotType.ObjectSlot)
                {
                    if (!o.Loaded)
                    {
                        continue;
                    }
                    Debug.Assert(loadedObjects.Contains(o.TheTpmHandle));
                    numLoadedObject++;
                }

                if (o.TheSlotType == SlotType.SessionSlot)
                {
                    if (o.Loaded)
                    {
                        Debug.Assert(loadedSessions.Contains(o.TheTpmHandle));
                        numLoadedSession++;
                    }
                    else
                    {
                        TpmHandle translatedHandle = new TpmHandle(Ht.LoadedSession, o.Context.savedHandle.GetOffset());
                        Debug.Assert(contextSavedSessions.Contains(translatedHandle));
                        numSavedSession++;
                    }
                }
            }

            Debug.Assert(numLoadedObject == loadedObjects.Length);
            Debug.Assert(numLoadedSession == loadedSessions.Length);
            Debug.Assert(numSavedSession == contextSavedSessions.Length);
        }

        internal enum SlotType
        {
            NoSlot,
            SessionSlot,

            /// <summary>
            /// Object or sequence that occupies a slot.
            /// </summary>
            ObjectSlot,
        }

        /// <summary>
        /// TbsContext provides a virtualized device interface to the underlying TPM. 
        /// It is typically instantiated via Tbs.GetTpm() or Tbs.CreateTpmContext().
        /// </summary>
        public sealed class TbsContext : Tpm2Device
        {
            private readonly Tbs Tbs;

            public TbsContext(Tbs associatedTbs)
            {
                Tbs = associatedTbs;
            }

            public override void DispatchCommand(CommandModifier active, byte[] inBuf, out byte[] outBuf)
            {
                Tbs.DispatchCommand(this, active, inBuf, out outBuf);
            }

            public override void Connect()
            {
                lock (Tbs)
                    Tbs.TpmDevice.Connect();
            }

            public override void Close()
            {
                lock (Tbs)
                    Tbs.TpmDevice.Close();
            }

            public override bool PlatformAvailable()
            {
                return Tbs.TpmDevice.PlatformAvailable();
            }

            public override bool PowerCtlAvailable()
            {
                return Tbs.TpmDevice.PowerCtlAvailable();
            }

            public override bool LocalityCtlAvailable()
            {
                return Tbs.TpmDevice.LocalityCtlAvailable();
            }

            public override bool NvCtlAvailable()
            {
                return Tbs.TpmDevice.NvCtlAvailable();
            }

            public override void PowerCycle()
            {
                lock (Tbs)
                    Tbs.TpmDevice.PowerCycle();
                Tbs.ProcessTpmClear(this);
            }

            public override void AssertPhysicalPresence(bool assertPhysicalPresence)
            {
                lock (Tbs)
                    Tbs.TpmDevice.AssertPhysicalPresence(assertPhysicalPresence);
            }

            public override bool ImplementsPhysicalPresence()
            {
                return Tbs.TpmDevice.ImplementsPhysicalPresence();
            }

            public override bool UsesTbs()
            {
                return Tbs.TpmDevice.UsesTbs();
            }

            public override bool HasRM()
            {
                return true;
            }

            public override void SignalHashStart()
            {
                lock (Tbs)
                    Tbs.TpmDevice.SignalHashStart();
            }

            public override void SignalHashData(byte[] data)
            {
                lock (Tbs)
                    Tbs.TpmDevice.SignalHashData(data);
            }

            public override void SignalHashEnd()
            {
                lock (Tbs)
                    Tbs.TpmDevice.SignalHashEnd();
            }

            public override void TestFailureMode()
            {
                lock (Tbs)
                    Tbs.TpmDevice.TestFailureMode();
            }

            public override UIntPtr GetHandle(UIntPtr h)
            {
                lock (Tbs)
                    return Tbs.TpmDevice.GetHandle(h);
            }

            public override void CancelContext()
            {
                lock (Tbs)
                    Tbs.TpmDevice.CancelContext();
            }

            public override bool ImplementsCancel()
            {
                return Tbs.TpmDevice.ImplementsCancel();
            }

            public override void SignalCancelOn()
            {
                lock (Tbs)
                    Tbs.TpmDevice.SignalCancelOn();
            }

            public override void SignalCancelOff()
            {
                lock (Tbs)
                    Tbs.TpmDevice.SignalCancelOff();
            }

            public override void SignalNvOn()
            {
                lock (Tbs)
                    Tbs.TpmDevice.SignalNvOn();
            }

            public override void SignalNvOff()
            {
                lock (Tbs)
                    Tbs.TpmDevice.SignalNvOff();
            }

            public override void SignalKeyCacheOn()
            {
                Tbs.TpmDevice.SignalKeyCacheOn();
            }

            public override void SignalKeyCacheOff()
            {
                Tbs.TpmDevice.SignalKeyCacheOff();
            }

            protected sealed override void Dispose(bool disposing)
            {
                if (disposing)
                {
                    Tbs.DisposeContext(this);
                }
                base.Dispose(disposing);
            }
        } // sealed class TbsContext
    }
}

/*++

Copyright (c) 2010-2015 Microsoft Corporation
Microsoft Confidential

*/
using System;
using System.Diagnostics;
using System.Collections.Generic;
using System.Linq;

/*
 * This file and the associated SlotContext.cs contains three classes that together perform
 * TPM "handle management." TbsContext implements an Tpm2Device interface to Tbs beneath.
 * Typically the programmer will use this as the device underneath a Tpm2. There is one of
 * these per TPM client. Tbs does the actual handle management.  
 * 
 * ObjectContextManager encapsulates the state for TPM clients.
 * 
 */

namespace Tpm2Lib
{
    /// <summary>
    /// Instances of the class TPM are created on top of TPM devices (either a physical TPM, or another Tbs)
    /// via new Tbs(theTpmDevice). TPM device contexts are then typically created through GetTpm(int locality).
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
        /// If probability is not 0.0, the SlotManager will randomly cycle the TPM through a simulated S3 transition at the 
        /// start of DispatchCommand (before the requested command is invoked).
        /// 
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
        /// Dispatch a command to the underlying TPM. This method implements all significant functionality.
        /// DispatchCommand examines the command stream and performs (approximately) the following functions
        /// 1) If the command references a handle (session or transient object) then TBS makes sure that the entity 
        ///     is loaded. If it is, then the handle is "translated" to the underlying TPM handle. If it is not, then
        ///     TBS checks to see if it has a saved context for the entity, and if so loads it.
        /// 2) If the command will fill a slot, then TBS ensures that a slot is available. It does this by ContextSaving
        ///     the LRU entity of the proper type (that is not used in this command).
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
                bool legalCommand = CommandProcessor.CrackCommand(inBuf, out commandHeader, out inHandles, out inSessions, out commandParmsNoHandles);

                if (!legalCommand)
                {
                    // Is a diagnostics command.  Pass through to TPM (a real RM would refuse).
                    TpmDevice.DispatchCommand(active, inBuf, out outBuf);
                    return;
                }

                TpmCc commandCode = commandHeader.CommandCode;

                // Lookup command
                CommandInfo command = Tpm2.CommandInfoFromCommandCode(commandCode);
                if (command == null)
                {
                    throw new Exception("Unrecognized command");
                }

                if (commandCode == TpmCc.ContextLoad || commandCode == TpmCc.ContextSave)
                {
                    //throw new Exception("ContextLoad and ContextSave not supported in this build");
                    Console.Error.WriteLine("ContextLoad and ContextSave not supported in this build");
                    outBuf = Marshaller.GetTpmRepresentation(new Object[] {
                        TpmSt.NoSessions,
                        (uint)10,
                        TpmRc.NotUsed });
                }

                // Look up referenced objects and sessions
                ObjectContext[] neededObjects = GetReferencedObjects(caller, inHandles);
                ObjectContext[] neededSessions = GetSessions(caller, inSessions);
                if (neededObjects == null || neededSessions == null)
                {
                    // This means that one or more of the handles was not registered for the context
                    byte[] ret = FormatError(TpmRc.Handle);
                    outBuf = ret;
                    return;
                }

                // Load referenced objects and sessions (free slots if needed)
                bool loadOk = LoadEntities(neededObjects);
                bool loadOk2 = LoadEntities(neededSessions);
                if (!loadOk || !loadOk2)
                {
                    throw new Exception("Failed to make space for objects or sessions at to execute command");
                }

                // At this point everything referenced should be loaded, and there will be a free slot if needed
                // so we can translate the input handles to the underlying handles 
                ReplaceHandlesIn(inHandles, inSessions, neededObjects, neededSessions);

                // create the translated command from the various components we have been manipulating
                byte[] commandBuf = CommandProcessor.CreateCommand(commandHeader.CommandCode, inHandles, inSessions, commandParmsNoHandles);
                Debug.Assert(commandBuf.Length == inBuf.Length);

                byte[] responseBuf;
                
                // Todo: Virtualize GetCapability for handle enumeration.

                //
                // Execute command on underlying TPM device.
                // If we get an ObjectMemory or SessionMemory error we try to make more space and try again
                // Note: If the TPM device throws an error above we let it propagate out.  There should be no side 
                // effects on TPM state that the TBS cares about.
                //
                do
                {
                    TpmDevice.DispatchCommand(active, commandBuf, out responseBuf);
                    TpmRc resCode = GetResultCode(responseBuf);
                    if (resCode == TpmRc.Success)
                    {
                        break;
                    }
                    if (resCode == TpmRc.ObjectMemory)
                    {
                        bool slotMade = MakeSpace(SlotType.ObjectSlot, neededObjects);
                        if (!slotMade)
                        {
                            throw new Exception("Failed to make an object slot in the TPM");
                        }
                        continue;
                    }
                    if (resCode == TpmRc.SessionMemory)
                    {
                        bool slotMade = MakeSpace(SlotType.SessionSlot, neededSessions);
                        if (!slotMade)
                        {
                            throw new Exception("Failed to make a session slot in the TPM");
                        }
                        continue;
                    }
                    break;
                } while (true);

                // Parse the response from the TPM
                // TODO: Make this use the new methods in Tpm2

                // ReSharper disable once UnusedVariable
                var mOut = new Marshaller(responseBuf);
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

                // If we have an error there is no impact on the loaded sessions, but we update
                // the LRU values because the user will likely try again.
                if (resultCode != TpmRc.Success)
                {
                    outBuf = responseBuf;
                    UpdateLastUseCount(new[] {neededObjects, neededSessions});
                    return;
                }

                // Update TBS database with any newly created TPM objects
                ProcessUpdatedTpmState(caller, command, responseHandles, neededObjects);

                // And if there were any newly created objects use the new DB entries to translate the handles
                ReplaceHandlesOut(responseHandles);
                byte[] translatedResponse = CommandProcessor.CreateResponse(resultCode, responseHandles, responseSessions, responseParmsNoHandles);

                outBuf = translatedResponse;
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
        private void ProcessUpdatedTpmState(TbsContext caller, CommandInfo command, TpmHandle[] responseHandles, ObjectContext[] inputObjects)
        {
            switch (command.CommandCode)
            {
                // Commands that fill a slot (apart from contextLoad, which is more complex)
                case TpmCc.Load:
                case TpmCc.LoadExternal:
                case TpmCc.CreatePrimary:
                case TpmCc.HmacStart:
                case TpmCc.HashSequenceStart:
                case TpmCc.StartAuthSession:
                    var t = new TpmHandle(responseHandles[0].handle);
                    // ReSharper disable once UnusedVariable
                    ObjectContext context2 = ContextManager.CreateObjectContext(caller, t);
                    break;
                case TpmCc.ContextLoad:
                case TpmCc.ContextSave:
                    throw new Exception("should not be here");
                case TpmCc.FlushContext:
                case TpmCc.SequenceComplete:
                    ContextManager.Remove(inputObjects[0]);
                    break;
                case TpmCc.EventSequenceComplete:
                    ContextManager.Remove(inputObjects[1]);
                    break;
            }
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
                Console.ForegroundColor = ConsoleColor.Magenta;
                Console.Error.Write(message);
                Console.ResetColor();
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
        /// TPM Debug support. Cycle the TPM through (a) SaveContext all loaded contexts, (b) StateSave(), (c) powerOff, (d) powerOn
        /// (e) Startup(SU_State) or Startup(S_CLEAR). Then needed objects and sessions will be paged back in as needed. This command
        /// is NOT thread safe. This command has side-effects on the startup counter and will likely result in clock discontinuities.
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
        /// Removes all TPM objects referenced by this context and then removes the context from the TBS database.
        /// </summary>
        /// <param name="c"></param>
        public void DisposeContext(TbsContext c)
        {
            lock (this)
            {
                foreach (ObjectContext o in ContextManager.ObjectContexts)
                {
                    if (o.Owner != c)
                    {
                        continue;
                    }
                    if (o.Loaded)
                    {
                        Tpm.FlushContext(o.TheTpmHandle);
                        o.Loaded = false;
                    }
                    else
                    {
                        if (o.TheSlotType == SlotType.SessionSlot)
                        {
                            // TODO: Need to flush saved sessions?
                            // Tpm.FlushContext(o.TheTpmHandle);
                            Tpm.FlushContext(o.Context.savedHandle);
                        }
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
                    if (!o.Loaded && o.TheSlotType == SlotType.SessionSlot && ((Object)o.TheTpmHandle) != null)
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
            return (from t in neededContexts where !t.Loaded select LoadObject(t, neededContexts)).All(loaded => loaded);
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
                contextToLoad.TheTpmHandle = Tpm._AllowErrors().ContextLoad(contextToLoad.Context);
                if (Tpm._LastCommandSucceeded())
                {
                    break;
                }
                bool spaceMade = MakeSpace(contextToLoad.TheSlotType, doNotEvict);
                if (!spaceMade)
                {
                    return false;
                }
            } while (true);
            contextToLoad.Loaded = true;
            return true;
        }

        /// <summary>
        /// Make a space in the TPM for an entity of type neededSlot (while not evicting another needed entity)
        /// </summary>
        /// <param name="neededSlot"></param>
        /// <param name="doNotEvict"></param>
        private bool MakeSpace(SlotType neededSlot, ObjectContext[] doNotEvict)
        {
            ObjectContext entityToEvict = ContextManager.GetBestEvictionCandidate(neededSlot, doNotEvict);
            if (entityToEvict == null)
            {
                return false;
            }
            // Candidate is the entity that we need to evict. Save it, and then update our internal database.
            Context b = Tpm.ContextSave(entityToEvict.TheTpmHandle);
            entityToEvict.Context = b;
            // Non-session objects evict on their own.  Transient objects need to be evicted explictly.
            // TODO: Manage the saved-context array.
            if (neededSlot != SlotType.SessionSlot)
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
        /// <param name="handles"></param>
        /// <param name="sessions"></param>
        /// <param name="theObjects"></param>
        /// <param name="theSessions"></param>
        private void ReplaceHandlesIn(TpmHandle[] handles, SessionIn[] sessions, ObjectContext[] theObjects, ObjectContext[] theSessions)
        {
            for (int j = 0; j < handles.Length; j++)
            {
                handles[j] = theObjects[j].TheTpmHandle;
            }
            for (int j = 0; j < sessions.Length; j++)
            {
                sessions[j].handle = theSessions[j].TheTpmHandle;
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

            internal TbsContext(Tbs associatedTbs)
            {
                Tbs = associatedTbs;
            }

            public override void DispatchCommand(CommandModifier active, byte[] inBuf, out byte[] outBuf)
            {
                Tbs.DispatchCommand(this, active, inBuf, out outBuf);
            }

            protected sealed override void Dispose(bool disposing)
            {
                if (disposing)
                {
                    Tbs.DisposeContext(this);
                }
                base.Dispose(disposing);
            }

            public override void PowerCycle()
            {
                throw new Exception("Device does not implement PowerCycle");
            }

            public override void AssertPhysicalPresence(bool assertPhysicalPresence)
            {
                throw new NotImplementedException("Device does not suport PP");
            }
        }
    }
}

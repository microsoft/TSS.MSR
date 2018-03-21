/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Diagnostics;
using System.Collections.Generic;
using System.Linq;

namespace Tpm2Lib
{
    internal class ObjectContextManager
    {
        public List<ObjectContext> ObjectContexts;

        /// <summary>
        /// UseCount enables LRU key evictiction.
        /// </summary>
        /// <returns></returns>
        public UInt64 GetUseCount()
        {
            return UseCount++;
        }

        private UInt64 UseCount;
        private const int NumObjectSlots = 3;
        private const int NumSessionSlots = 3;
        private const bool TraceStateChanges = false;

        internal ObjectContextManager()
        {
            ObjectContexts = new List<ObjectContext>();
        }

        public void Remove(ObjectContext c)
        {
            ObjectContexts.Remove(c);
        }

        /// <summary>
        /// Remove all contexts associated with a client (to support client disconnect).
        /// </summary>
        /// <param name="owner"></param>
        public void RemoveAll(Tbs.TbsContext owner)
        {
            ObjectContexts.RemoveAll(item => item.Owner == owner);
        }

        public void RemoveAll()
        {
            ObjectContexts.Clear();
        }

        /// <summary>
        /// Find the assiged TBS handle for a loaded TPM entity.
        /// </summary>
        /// <param name="tpmHandle"></param>
        /// <returns></returns>
        internal uint TbsHandleFromTpmHandle(uint tpmHandle)
        {
            return ObjectContexts.Find(item => 
                ((Object)item.TheTpmHandle) != null && item.TheTpmHandle.handle == tpmHandle).OwnerHandle.handle;
        }

        internal int NumFreeSlots(Tbs.SlotType neededSlot)
        {
            int numUsedSlotsOfType = ObjectContexts.Sum(item => (item.Loaded && (item.TheSlotType == neededSlot)) ? 1 : 0);
            switch (neededSlot)
            {
                case Tbs.SlotType.ObjectSlot:
                    return NumObjectSlots - numUsedSlotsOfType;
                case Tbs.SlotType.SessionSlot:
                    return NumSessionSlots - numUsedSlotsOfType;
                default:
                    throw new Exception("NumFreeSlots: Should not be here");
            }
        }

        internal ObjectContext CreateObjectContext(Tbs.TbsContext owner, TpmHandle tpmHandle)
        {
            Tbs.SlotType newSlotType = Tbs.SlotTypeFromHandle(tpmHandle);
            if (newSlotType == Tbs.SlotType.NoSlot)
            {
                throw new Exception("CreateObjectContext: Should not be here");
            }

            // Make a new slot context of the requisite type
            uint tbsHandle = GetFreeHandle(owner, tpmHandle);
            var newContext = new ObjectContext {
                OwnerHandle = new TpmHandle(tbsHandle),
                TheTpmHandle = tpmHandle,
                TheSlotType = newSlotType,
                LastUseCount = GetUseCount(),
                Loaded = true,
                Owner = owner
            };

            ObjectContexts.Add(newContext);
            return newContext;
        }

        /// <summary>
        /// This TBS returns a random handle value in the desired handle range (ugh).
        /// </summary>
        /// <param name="owner"></param>
        /// <param name="tpmHandle"></param>
        /// <returns></returns>
        private uint GetFreeHandle(Tbs.TbsContext owner, TpmHandle tpmHandle)
        {
            Tbs.SlotType neededType = Tbs.SlotTypeFromHandle(tpmHandle);
            if (neededType == Tbs.SlotType.NoSlot)
            {
                return tpmHandle.handle;
            }

            uint candidateHandle = tpmHandle.handle;
            int numTries = 0;
            while (numTries++ < 1000)
            {
                if (!HandleInUse(owner, candidateHandle))
                    return candidateHandle;

                Ht handleType = tpmHandle.GetType();
                var randomPos = (uint)Globs.GetRandomInt((int)TpmHandle.GetRangeLength(tpmHandle.GetType()));
                candidateHandle = ((uint)handleType << 24) + randomPos;
            }
            throw new Exception("Too many TBS contexts");
        }

        private bool HandleInUse(Tbs.TbsContext owner, uint handle)
        {
            return ObjectContexts.Find(item => (item.Owner == owner && item.OwnerHandle.handle == handle)) != null;
        }

        internal ObjectContext GetContext(Tbs.TbsContext caller, TpmHandle callerHandle)
        {
            if (Tbs.SlotTypeFromHandle(callerHandle) == Tbs.SlotType.NoSlot)
            {
                // Indicates that this is a TPM resident object (NV-slot, primary-handle, PWAP-handle, etc.)
                var temp = new ObjectContext {TheTpmHandle = callerHandle};
                return temp;
            }

            ObjectContext x = ObjectContexts.Find(item => (item.Owner == caller) 
                                        && item.OwnerHandle.handle == callerHandle.handle);
            return x;   // x may be null
        }

        /// <summary>
        /// Gets the best eviction candidate for entities of given type.  May return NULL.
        /// Pinned entities are the ones used by the current command.
        /// </summary>
        /// <param name="type"></param>
        /// <param name="pinnedEntities"></param>
        /// <returns></returns>
        internal ObjectContext GetEntityToEvict(Tbs.SlotType type,
                                                ObjectContext[] pinnedEntities)
        {
            ObjectContext candidate = null;
            foreach (ObjectContext c in ObjectContexts)
            {
                // See if this context is a candidate for eviction
                if (c.TheSlotType != type || !c.Loaded || pinnedEntities.Contains(c))
                {
                    continue;
                }

                // ObjectContext c is a candidate for removal.  If we don't already
                // have a candidate then see if the new candidate is staler.
                if (candidate == null || c.LastUseCount < candidate.LastUseCount)
                {
                    candidate = c;
                }
            }
            return candidate;
        }

        /// <summary>
        /// Returns a unique identifier of the re-saved session context, or 0 if no
        /// suitable one was found.
        /// </summary>
        internal ObjectContext GetOldestSavedSession()
        {
            foreach (ObjectContext c in ObjectContexts)
            {
                if (c.TheSlotType == Tbs.SlotType.SessionSlot && !c.Loaded)
                    return c;
            }
            return null;
        }
    } // class ObjectContextManager

    internal class ObjectContext
    {
        internal Tbs.SlotType TheSlotType { get; set; }

        /// <summary>
        /// Use count 
        /// </summary>
        internal UInt64 LastUseCount { get; set; }

        internal Tbs.TbsContext Owner { get; set; }

        public TpmHandle OwnerHandle { get; set; }

        /// <summary>
        /// EntityHandle is null if the entity is not loaded.
        /// </summary>
        internal TpmHandle TheTpmHandle { get; set; }

        /// <summary>
        /// ContextBlob is null if the entity has not yet been ContextSave'd.
        /// </summary>
        internal Context Context;

        // ReSharper disable once InconsistentNaming
        private bool _Loaded;

        internal bool Loaded
        {
            get
            {
                // Is it a fixed TPM resource? (i.e., always loaded).
                if ((Object)TheTpmHandle == null)
                {
                    return false;
                }
                Ht tp = TheTpmHandle.GetType();
                if (!(tp == Ht.HmacSession || tp == Ht.PolicySession || (tp == Ht.Transient)))
                {
                    return true;
                }
                return _Loaded;
            }
            set
            {
                _Loaded = value;
            }
        }

        public override string ToString()
        {
            return String.Format("Owner:{0:x}, OwnerHandle:{1:x}, TpmHandle:{2:x}, " +
                                 "Loaded:{3:x}, Type:{4}",
                                 Owner, OwnerHandle.handle,
                                 ((Object)TheTpmHandle != null) ? TheTpmHandle.handle : 0,
                                 Loaded,
                                 TheSlotType.ToString());
        }
    }
}

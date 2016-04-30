/*++

Copyright (c) 2010-2015 Microsoft Corporation
Microsoft Confidential

*/
using System;
using System.Linq;
using System.Runtime.Serialization;
using System.Diagnostics;

namespace Tpm2Lib
{
    public enum Auth
    {
        None,
        Hmac,
        Pw,
        Default
    }

    internal enum Direction
    {
        /// <summary>
        /// To TPM.
        /// </summary>
        Command,

        /// <summary>
        /// From TPM.
        /// </summary>
        Response
    }

    /// <summary>
    /// Base class for PWAP and HMAC sessions. Policy sessions are a flavor of HMAC sessions.
    /// </summary>
    public class SessionBase
    {
        /// <summary>
        /// Session handle
        /// </summary>
        public TpmHandle Handle;

        /// <summary>
        /// Handle to authorize
        /// </summary>
        internal TpmHandle AuthHandle;

        /// <summary>
        /// Session type indicator corresponding to the Auth.None authorization type.
        /// It is never used as a session itself. Instead it serves as a placeholder
        /// in a session list provided by the user, to specify the requirement not to
        /// use any authorization for the corresponding handle. Normally this indicator
        /// is useful for debugging purposes only.
        /// </summary>
        internal static SessionBase None = new SessionBase();

        /// <summary>
        /// Session type indicator corresponding to the Auth.Hmac authorization type.
        /// It is never used as a session itself. Instead it serves as a placeholder
        /// in a session list provided by the user, and is replaced by an HMAC
        /// authorization session by the library when command buffer is generated.
        /// Consider using Auth.Default instead.
        /// </summary>
        internal static SessionBase Hmac = new SessionBase();

        /// <summary>
        /// Session type indicator corresponding to the Auth.Hmac authorization type.
        /// It is never used as a session itself. Instead it serves as a placeholder
        /// in a session list provided by the user, and is replaced by a password
        /// authorization session by the library when command buffer is generated.
        /// Consider using Auth.Default instead.
        /// </summary>
        internal static SessionBase Pw = new SessionBase();

        /// <summary>
        /// Session type indicator corresponding to the Auth.Hmac authorization type.
        /// It is never used as a session itself. Instead it serves as a placeholder
        /// in a session list provided by the user, and is replaced by either HMAC or
        /// password authorization session by the library when command buffer is generated.
        /// The type of the actual auth session is determined by the TPM device interface
        /// associated with the tpm2 object used to issue the command. Normally remote
        /// TPM device would use HMAC session, while local TPM device would use password
        /// authorization.
        /// </summary>
        internal static SessionBase Default = null;

        /// <summary>
        /// Checks if the given reference is a placeholder indicating the type
        /// of authorization to be used in command buffer in its stead.
        /// </summary>
        internal static bool IsPlaceholder(SessionBase s)
        {
            return s == Default || s == Hmac || s == Pw;
        }

        /// <summary>
        /// Returns password session with the specified authorization value.
        /// </summary>
        public static implicit operator SessionBase(byte[] authValue)
        {
            return new Pwap(authValue);
        }

        /// <summary>
        /// Returns password session with the specified authorization value.
        /// </summary>
        public static implicit operator SessionBase(AuthValue a)
        {
            return a.AuthVal;
        }

        /// <summary>
        /// Returns placeholder session object for the specified type.
        /// </summary>
        public static implicit operator SessionBase(Auth authType)
        {
            switch(authType)
            {
                case Auth.None: return None;
                case Auth.Hmac: return Hmac;
                case Auth.Pw: return Pw;
            }
            return Default;
        }

        public static implicit operator TpmHandle(SessionBase s)
        {
            return s.Handle;
        }

        protected SessionBase()
        {
        }
    }

    /// <summary>
    /// AuthSession encapsulates HMAC, policy, encryption/decryption, and audit sessions,
    /// i.e. all session types that are represented by a TPM handle created by means of
    /// TPM2_StartAuthSession command.
    /// </summary>
    public class AuthSession : SessionBase
    {
        /// <summary>
        /// Placeholder value indicating that an unencrypted salt value must be supplied.
        /// </summary>
        private static byte[] SaltNeeded = new byte[0];

        /// The following set of parameters defines a session state. All of them except
        /// for NonceTpm are specified as TPM2_StartAuthSession command parameters,
        /// and initial value of NonceTpm is returned by this command. NonceCaller
        /// and NonceTpm will then be updated correspondingly before and after each
        /// command using the session.
#region Session parameters
        public TpmSe       SessionType;
        public byte[]      Salt;
        public TpmHandle   BindObject;
        public byte[]      NonceCaller;
        public byte[]      NonceTpm;

        /// <summary>
        /// Symmetric cipher to be used for encrypting and decrypting sessions.  
        /// </summary>
        public SymDef      Symmetric;

        /// <summary>
        /// Hash algorithm used by this session.
        /// </summary>
        public TpmAlgId    AuthHash;
#endregion
            
        public SessionAttr Attrs;

        public byte[] SessionKey;

        /// <summary>
        /// By default policy sessions do NOT include the authValue of the associated entity in 
        /// the HMAC. The caller can add it in by calling PolicyAuthValue.
        /// </summary>
        internal bool SessIncludesAuth;

        /// <summary>
        /// If SessIncludesAuth is true, then PlaintextAuth implies that the authVal is used like
        /// PWAP. Else the hmac computation is performed.  
        /// </summary>
        internal bool PlaintextAuth = false;

        protected AuthSession()
        {
        }

        /// <summary>
        /// Constructs an object encapsulating a session opened in TPM. The Tpm2 object
        /// that was used to create the session tracks other information associated
        /// with it and uses it to compute session key and command/response HMAC.
        /// </summary>
        public AuthSession(TpmHandle h)
        {
            if (!h.IsSession())
            {
                Globs.Throw<ArgumentException>("AuthSession: Attempt to construct from non-session handle");
            }
            Handle = h;
        }

        public static implicit operator AuthSession(TpmHandle sessionHandle)
        {
            return new AuthSession(sessionHandle);
        }

        public AuthSession(ParametrizedHandle ph)
        {
            if (ph.Handle != TpmRh.None && !ph.Handle.IsSession())
            {
                Globs.Throw<ArgumentException>("AuthSession: Attempt to construct from parametrized non-session handle");
            }
            Handle = ph.Handle;
            foreach(object param in ph.Params)
            {
                if (param is SessionAttr)
                {
                    Attrs = (SessionAttr)param;
                }
                else if (param is byte[])
                {
                    Salt = (byte[])param;
                }
                else if (param != null)
                {
                    Globs.Throw<ArgumentException>("AuthSession: Attempt to construct from malformed parametrized handle");
                }
            }
        }

        public static implicit operator AuthSession(ParametrizedHandle sessionHandle)
        {
            return new AuthSession(sessionHandle);
        }


        /// <summary>
        /// Constructs a temporary object to hold parameters of a session.
        /// Intended only for internal use by the Tpm2 class.
        /// </summary>
        internal AuthSession(TpmSe sessionType, TpmHandle tpmKey, TpmHandle bindObject,
                             byte[] nonceCaller, byte[] nonceTpm, SymDef symmetric, TpmAlgId authHash)
        {
            SessionType = sessionType;
            Salt = tpmKey == TpmRh.Null ? null : SaltNeeded;
            BindObject = bindObject;
            NonceCaller = nonceCaller;
            NonceTpm = nonceTpm;
            Symmetric = symmetric;
            AuthHash = authHash;
        }

        /// <summary>
        /// Sets parameters associated with the session.
        /// </summary>
        internal void Init (AuthSession Params)
        {
            SessionType = Params.SessionType;
            BindObject = Params.BindObject;
            NonceCaller = Params.NonceCaller;
            NonceTpm = Params.NonceTpm;
            Symmetric = Params.Symmetric;
            AuthHash = Params.AuthHash;
            AuthHandle = Params.AuthHandle;
            // When salt is required, destination session will have it set directly by the user
            if (Params.Salt != SaltNeeded)
                Salt = null;
        }

        /// <summary>
        /// Returns true if the parameters associated with the session context in TPM
        /// have been set in this object.
        /// </summary>
        internal bool Initialized()
        {
            return NonceCaller != null;
        }

        public void NewNonceCaller()
        {
            // Make a new nonce as big as the last
            NonceCaller = Globs.GetRandomBytes(NonceCaller.Length);
        }

        public void SetNonceTpm(byte[] nonceTpm)
        {
            NonceTpm = Globs.CopyData(nonceTpm);
        }

        /// <summary>
        /// Checks whether the given session can be used for parameter encryption.
        /// </summary>
        internal bool CanEncrypt()
        {
            return Symmetric != null && Symmetric.Algorithm != TpmAlgId.Null;
        }

        internal byte[] ParmEncrypt(byte[] parm, Direction inOrOut)
        {
            if (Symmetric == null)
            {
                Globs.Throw("parameter encryption cipher not defined");
                return parm;
            }
            if (Symmetric.Algorithm == TpmAlgId.Null)
            {
                return parm;
            }

            byte[] nonceNewer, nonceOlder;
            if (inOrOut == Direction.Command)
            {
                nonceNewer = NonceCaller;
                nonceOlder = NonceTpm;
            }
            else
            {
                nonceNewer = NonceTpm;
                nonceOlder = NonceCaller;
            }

            byte[] encKey = (AuthHandle != null && AuthHandle.Auth != null)
                                ? SessionKey.Concat(Globs.TrimTrailingZeros(AuthHandle.Auth)).ToArray()
                                : SessionKey;

            if (Symmetric.Algorithm == TpmAlgId.Xor)
            {
                return CryptoLib.KdfThenXor(AuthHash, encKey, nonceNewer, nonceOlder, parm);
            }

            int keySize = (Symmetric.KeyBits + 7) / 8,
                blockSize = SymmCipher.GetBlockSize(Symmetric),
                bytesRequired = keySize + blockSize;

            byte[] keyInfo = KDF.KDFa(AuthHash, encKey, "CFB", nonceNewer, nonceOlder, bytesRequired * 8);

            var key = new byte[keySize];
            Array.Copy(keyInfo, 0, key, 0, keySize);

            var iv = new byte[blockSize];
            Array.Copy(keyInfo, keySize, iv, 0, blockSize);

            // Make a new SymmCipher from the key and IV and do the encryption.
            using (SymmCipher s = SymmCipher.Create(Symmetric, key, iv))
            {
                return inOrOut == Direction.Command ? s.Encrypt(parm) : s.Decrypt(parm);
            }
        }

        /// <summary>
        /// Calculate the session-key from the nonces and salt/bound values (if present)
        /// </summary>
        internal void CalcSessionKey()
        {
            Debug.Assert(SessionKey == null, "Attempt to repeatedly calculate session key");

            if (Salt == SaltNeeded)
            {
                Globs.Throw(string.Format("Unencrypted salt value must be provided for the session {0:x}", Handle.handle));
            }

            // Compute Handle.Auth in accordance with Part 1, 19.6.8.
            if (Salt == null && BindObject == TpmRh.Null)
            {
                SessionKey = new byte[0];
                return;
            }

            byte[] auth = Globs.TrimTrailingZeros(BindObject.Auth);
            byte[] hmacKey = Globs.Concatenate(auth, Salt);
            SessionKey = KDF.KDFa(AuthHash, hmacKey, "ATH", NonceTpm, NonceCaller,
                                  TpmHash.DigestSize(AuthHash) * 8);
        }

        /// <summary>
        /// Calculate and return the auth-hmac (or plaintext auth if it is a policy session with PlaintextAuth set)
        /// based on the current session parms.
        /// </summary>
        /// <param name="parmHash"></param>
        /// <param name="direction"></param>
        /// <param name="nonceDec"></param>
        /// <param name="nonceEnc"></param>
        /// <returns></returns>
        internal byte[] GetAuthHmac(byte[] parmHash, Direction direction, byte[] nonceDec = null, byte[] nonceEnc = null)
        {
            // special case.  If this is a policy session and the session includes PolicyPassword the 
            // TPM expects and assumes that the HMAC field will have the plaintext entity field as in 
            // a PWAP session (the related PolicyAuthValue demands an HMAC as usual)
            if (PlaintextAuth)
            {
                return Handle.Auth ?? AuthHandle.Auth;
            }

            byte[] nonceNewer, nonceOlder;
            if (direction == Direction.Command)
            {
                nonceNewer = NonceCaller;
                nonceOlder = NonceTpm;
            }
            else
            {
                nonceNewer = NonceTpm;
                nonceOlder = NonceCaller;
            }
            byte[] sessionAttrs = Marshaller.GetTpmRepresentation(Attrs);

            byte[] auth = Handle.Auth;
            if (AuthHandle != null && Handle != TpmRh.TpmRsPw && auth == null &&
                ((SessionType != TpmSe.Policy && BindObject != AuthHandle) ||
                 (SessionType == TpmSe.Policy && SessIncludesAuth)))
            {
                auth = Globs.TrimTrailingZeros(AuthHandle.Auth);
            }
            byte[] hmacKey = Globs.Concatenate(SessionKey, auth);
            byte[] bufToHmac = Globs.Concatenate(new[] {parmHash, nonceNewer, nonceOlder,
                                                        nonceDec, nonceEnc, sessionAttrs});

            byte[] hmac = CryptoLib.HmacData(AuthHash, hmacKey, bufToHmac);
#if false
            Console.WriteLine(Globs.FormatBytesCompact("hmacKey: ", hmacKey));
            Console.WriteLine(Globs.FormatBytesCompact("nonceNewer: ", nonceNewer));
            Console.WriteLine(Globs.FormatBytesCompact("nonceOlder: ", nonceOlder));
            Console.WriteLine(Globs.FormatBytesCompact("nonceDec: ", nonceDec));
            Console.WriteLine(Globs.FormatBytesCompact("nonceEnc: ", nonceEnc));
            Console.WriteLine(Globs.FormatBytesCompact("attrs: ", sessionAttrs));
            Console.WriteLine(Globs.FormatBytesCompact("HMAC: ", hmac));
#endif
            return hmac;
        }

        /// <summary>
        /// Run a path on the policy tree.  The path is identified by the leaf identifier string. A session is
        /// created and returned. If allowErrors is true then errors returned do not cause an exception (but 
        /// are returned in the response code).
        /// </summary>
        /// <param name="tpm"></param>
        /// <param name="policySession"></param>
        /// <param name="branchToEvaluate"></param>
        /// <param name="allowErrors"></param>
        /// <returns></returns>
        public TpmRc RunPolicy(Tpm2 tpm, PolicyTree policyTree, string branchToEvaluate = null, bool allowErrors = false)
        {
            policyTree.AllowErrorsInPolicyEval = allowErrors;

            PolicyAce leafAce = null;

            // First, check that the policy is OK.
            policyTree.CheckPolicy(branchToEvaluate, ref leafAce);
            if (leafAce == null)
            {
                Globs.Throw("RunPolicy: Branch identifier " + branchToEvaluate + " does not exist");
            }

            var responseCode = TpmRc.Success;
            try
            {
                if (allowErrors)
                {
                    tpm._DisableExceptions();
                }

                tpm._InitializeSession(this);

                // Walk up the tree from the leaf..
                PolicyAce nextAce = leafAce;
                while (nextAce != null)
                {
                    responseCode = nextAce.Execute(tpm, this, policyTree);

                    if (responseCode != TpmRc.Success)
                    {
                        break;
                    }

                    // ..and continue along the path to the root
                    nextAce = nextAce.PreviousAce;
                }
            }
            finally
            {
                if (allowErrors)
                {
                    tpm._EnableExceptions();
                }
            }

            return responseCode;
        }
    } // class AuthSession

    /// <summary>
    /// Password authentication protocol session
    /// </summary>
    public class Pwap : SessionBase
    {
        public Pwap()
        {
            Handle = new TpmHandle(TpmRh.TpmRsPw);
        }

        public Pwap(byte[] authVal)
        {
            Handle = new TpmHandle(TpmRh.TpmRsPw);
            Handle.Auth = authVal;
        }
    } // class Pwap
}

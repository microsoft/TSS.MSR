/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

/// <summary>  Customized TPMT_HA implementation </summary>
class _DLLEXP_ TPMT_HA : public _TPMT_HA
{
public:
    TPMT_HA() {}

    operator const ByteVec&() const { return digest; }
    operator const TPM_ALG_ID() const { return hashAlg; }

    bool operator==(const TPMT_HA& rhs) const
    {
        return this == &rhs
            || (hashAlg == rhs.hashAlg && digest == rhs.digest);
    }
    bool operator!=(const TPMT_HA& rhs) const { return !(*this == rhs); }

    bool operator==(const ByteVec& rhs) const { return digest == rhs; }
    bool operator!=(const ByteVec& rhs) const { return digest != rhs; }

    /// <summary> Create a zero-bytes TPMT_HASH with the indicated hash-algorithm. </summary>
    TPMT_HA(TPM_ALG_ID alg);

    TPMT_HA(TPM_ALG_ID hashAlg, const ByteVec& digest) : _TPMT_HA(hashAlg, digest) {}

    virtual ~TPMT_HA() {}

    /// <summary> Create a TPMT_HA from the named-hash of the _data parameter. </summary>
    static TPMT_HA FromHashOfData(TPM_ALG_ID hashAlg, const ByteVec& data);

    // TODO: Unicode, etc.
    /// <summary> Create a TPMT_HA from the hash of the supplied-string. </summary>
    static TPMT_HA FromHashOfString(TPM_ALG_ID hashAlg, const string& str);

    /// <summary> Returns the digest size in bytes for the current hash algorithm. </summary>
    UINT16 DigestSize();

    /// <summary> Returns the digest size in bytes for the given hash algoruthm. </summary>
    static UINT16 DigestSize(TPM_ALG_ID hashAlg);

    /// <summary> Perform a TPM-extend operation on the current hash-value.  Note
    /// the TPM only accepts hash-sized vector inputs: this function has no such limitations. </summary>
    TPMT_HA& Extend(const ByteVec& x);

    /// <summary> Perform a TPM-event operation on this PCR-value (an event "extends" the hash of _x). </summary>
    TPMT_HA Event(const ByteVec& _x);

    void Reset();
}; // class TPMT_HA

inline bool operator==(const ByteVec& digest, const TPMT_HA& hash) { return digest == hash.digest; }
inline bool operator!=(const ByteVec& digest, const TPMT_HA& hash) { return digest != hash.digest; }

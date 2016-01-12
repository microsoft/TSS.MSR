/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/

/*
The definitions in this file are #included in TpmTypes.h

These are the custom additions for the TSS_KEY class
*/

#define TSS_KEY_CUSTOM_CLONE(l,r)

///<summary>Create a new software key based on the parameters in the publicPart.  Set the public key value in publicPart
/// and the private key in privatePart.</summary>
public:
void CreateKey();

///<summary>Sign the data _toSign based on the (default or overriden) scheme (signing keys only).</summary>
public:
SignResponse Sign(std::vector<BYTE>& _toSign, const TPMU_SIG_SCHEME& nonDefaultScheme);

///<summary>Decrypt _blob (decrypting keys/schemes only).</summary>
public:
std::vector<BYTE> Decrypt(std::vector<BYTE> _blob);
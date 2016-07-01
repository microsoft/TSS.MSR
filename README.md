# TSS.MSR

##The TPM Software Stack from Microsoft Research

The TPM or Trusted Platform Module is a security component found in many PCs and mobile devices. TPMs provide security functions in the areas of:

* Cryptographic key generation, protection, management, and use
* Cryptographic device identity
* Secure logging and log-reporting, i.e., attestation
* Secure non-volatile storage
* Other functions including hashing, random number generation, a secure clock, etc.

The Windows operating system relies on the TPM for a number of its security functions.  Examples include BitLocker™ drive encryption, the Windows Virtual Smart Card feature, and the Platform Crypto Provider.

Windows also exposes low-level programmatic access to the TPM through an interface called TPM Base Services (TBS).  Developers can use this interface together with TPM Software Stack (TSS) libraries to develop TPM-based applications.  However, the new TPM 2.0 is not compatible with earlier TPM libraries.  That is why we are providing new libraries, for C++ and .Net, to allow developers to write TPM 2.0-based applications.

##TSS.Net and TSS.C++

TSS.Net and TSS.C++ simplify writing Windows applications that use TPM 2.0.  These libraries provide low-level access to the TPM, and handle many of the complex issues that arise when interacting with the TPM.  The TSS.Net library is written in managed code (C#) and can be used on Windows 8+ systems by any managed application.  TSS.C++, also referred to as TSS.CPP, is written in C++ and provides the same functionality for native code.  These libraries are distributed in source code form and the package includes example applications that demonstrate the use of the libs and the underlying TPM.

In addition to supporting access to a physical TPM, TSS.MSR libraries can also be connected to a TPM simulator to enable application development and debugging on platforms that do not have a TPM 2.0 device.  The connection to the simulator is over a TCP/IP socket so the TPM simulator may be running on a remote machine or in another process on the same machine.  The TPM simulator binary is available [here](https://www.microsoft.com/en-us/download/details.aspx?id=52507).

##Platform Crypto Provider Toolkit

The TSS.MSR project also provides the TPM Platform Crypto Provider Toolkit.  It contains sample code, utilities, and documentation for using TPM-related functionality on Windows 8.x systems. It covers TPM-backed Crypto-Next-Gen (CNG) Platform Crypto Provider, and how attestation service providers can use the new Windows 8.x features. Both TPM 1.2 and TPM 2.0-based systems are supported.

We hope that the TSS.MSR project will prove useful to both software developers and researchers in their development of security solutions and applications for the Windows operating system.

Please send questions and feedback to tssdotnet@microsoft.com or tssdotcpp@microsoft.com.

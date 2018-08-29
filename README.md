# TSS.MSR

## TPM 2.0 ecosystem

Trusted Platform Module (TPM) is a security component forming roots of trust in many PCs, servers and mobile devices. TPMs provide security functionaility in the areas of:

* Cryptographic key generation, protection, management, and use
* Cryptographic device identity
* Secure logging and log-reporting, i.e., attestation
* Secure non-volatile storage
* Other functions including hashing, random number generation, a secure clock, etc.

Microsoft Windows operating system relies on the TPM for a number of its security functions.  Examples include BitLocker™ drive encryption, the Windows Virtual Smart Card feature, and the Platform Crypto Provider. Windows 10 [requires](https://docs.microsoft.com/en-us/windows/security/hardware-protection/tpm/tpm-recommendations#tpm-20-compliance-for-windows-10) TPM 2.0 to be enabled in all its desktop editions (Home, Pro, Enterprise, and Education) and in server editions running guarded fabric.

Both Windows and Linux operating systems expose low-level programmatic access to their TPM 2.0 devices. On Windows TPM 2.0 is avilable via TPM Base Services (TBS) API, and on Linux - via /dev/tpm0 or /dev/tpmrm0 device file abstractions.  For the purposes of TPM 2.0 application development it is extremely convenient to use the [TPM 2.0 simulator](https://github.com/Microsoft/ms-tpm-20-ref/tree/master/TPMCmd/Simulator) developed, open-sourced, and maintained on behalf of [TCG](http://trustedcomputinggroup.org) by Microsoft.


## TPM Software Stack (TSS) implementations from Microsoft

All flavors of TPM 2.0 devices mentioned in the previous section communicate with applications via a rather complex binary interface defined by the TCG's [TPM 2.0 specification](https://trustedcomputinggroup.org/resource/tpm-library-specification/) wrapped into OS/simulator specific protocols. Writing code for manual creation of the TPM 2.0 command buffers, parsing responce buffers, building HMAC and policy sessions, verifying audit data, etc., is extremely tedious, time consuming, and error prone task.

In order to facilitate the development of applications and services using TPM 2.0, Microsoft has developed a series of TSS implementations for different programming languages. All these implementations provide complete representation of the TPM 2.0 API (commands, data structures, enumerations, unions) using the means of the corersponding languages, and some of them - additional functionaility that greatly simplifies communications with TPM 2.0. All TSS.MSR implementations provide abstraction for Windows/Linux/Simulator TPM 2.0 devices.

### TSS.Net and TSS.CPP

TSS.Net and TSS.CPP are written in C# and C++ correspondingly, and are the richest TSS implementations in this collection. Besides comlete abstraction of the TPM 2.0 interface, they implement additional functionality, such as:
- automatic handling HMAC and policy sessions;
- expected audit, policy and CP-digests computation;
- object oriented representation of the policy commands;
- multiple helpers simplifying bridging between software crypto and TPM 2.0 based applications.

### TSS.Java and TSS.JS

These implementations are for Java language and Node.JS environment, and at the moment they provide complete abstraction of the TPM 2.0 interface without most of the rich capabilities of TSS.Net or TSS.CPP. Node.JS version is written in TypeScript language.

## Tpm2Tester

This repo also contains a framework for TPM 2.0 test suites creation. See [Tpm2Tester](Tpm2Tester/README.md) for more details.

## System Requirements

TSS.Net is a cross-platform .NET Standard library and requires Visual Studio 2015 Update 3 or above. It can be built to target one of the following .NET framework flavors: .NET 4.6, .NET Core 2.0 (for both Windows and Linux), .NET Standard, .NET UWP 10.0. You can download the latest versions of the .NET Framework [here](https://www.microsoft.com/net/download/windows).

TSS.Java targets Java SE 8 or above, and TSS.JS 

## Platform Crypto Provider Toolkit

The TSS.MSR project also provides the TPM Platform Crypto Provider Toolkit.  It contains sample code, utilities, and documentation for using TPM-related functionality on Windows 8.x systems. It covers TPM-backed Crypto-Next-Gen (CNG) Platform Crypto Provider, and how attestation service providers can use the new Windows 8.x features. Both TPM 1.2 and TPM 2.0-based systems are supported.

## See Also...
* Projects related to [Windows 10 IoT Core Security](https://github.com/ms-iot/security).
* Resource constrained TPM access lib for IoT, [Urchin](https://github.com/ms-iot/security/tree/master/Urchin).

## Questions and Feedback
We hope that the TSS.MSR project will prove useful to both software developers and researchers in their development of security solutions and applications for the Windows operating system.

Please send questions and feedback to tssdotnet@microsoft.com or tssdotcpp@microsoft.com.

# TSS.JS

This is an implementation of a TPM 2.0 Software Services (TSS) layer for Node.js/Typescript (TSS.JS). The purpose of a TSS layer is to provide a developer-friendly way of using TPM 2.0 across different platforms.

The given implementation supports physical TPM 2.0 devices (discrete, firmware or virtual) on Windows and Nix systems, as well as the TPM 2.0 simultor by Microsoft Research.

***

NOTE! This is an initial development version of TSS.JS (preview), and at the moment it is NOT intended for usage in production by parties outside of Microsoft. Microsoft developers shall coordinate the library usage with its authors.

***

BUILD CONFIGURATION

TSS.JS requires Node 4.8.4 or above on Windows and Linux.

TSS.JS on Windows uses native Win32 API, so it depends on the node-gyp based modules and requires native build tools to be available. The following build configurations have been tested on Windows:

1)	No Visual Studio and ‘npm i -g -production windows-build-tools’
2)	VS 2015
3)	VS 2017 on top of VS 2015
4)	VS 2017 with ‘VC++ 2015.3 v140 toolset for desktop (x86,x64)’ component installed (has to be manually selected on the 'Individual components tab in the installer window') and ‘npm config set msvs_version 2015’

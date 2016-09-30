using System;
using System.Linq;
using System.Threading.Tasks;
using Tpm2Lib;
using Windows.Foundation;
using Windows.UI.Core;
using Windows.UI.Popups;
using Windows.UI.Xaml.Controls;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=402352&clcid=0x409

namespace App1
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        public MainPage()
        {
            this.InitializeComponent();
        }
        
        /// <summary>
        /// This sample demonstrates the creation and use of TPM NV-storage
        /// </summary>
        /// <param name="tpm">Reference to TPM object.</param>
        void NVReadWrite(Tpm2 tpm)
        {
            //
            // AuthValue encapsulates an authorization value: essentially a byte-array.
            // OwnerAuth is the owner authorization value of the TPM-under-test.  We
            // assume that it (and other) auths are set to the default (null) value.
            // If running on a real TPM, which has been provisioned by Windows, this
            // value will be different. An administrator can retrieve the owner
            // authorization value from the registry.
            //
            var ownerAuth = new AuthValue();
            TpmHandle nvHandle = TpmHandle.NV(3001);

            //
            // Clean up any slot that was left over from an earlier run
            // 
            tpm[ownerAuth]._AllowErrors().NvUndefineSpace(TpmHandle.RhOwner, nvHandle);

            //
            // Scenario 1 - write and read a 32-byte NV-slot
            // 
            AuthValue nvAuth = AuthValue.FromRandom(8);
            tpm[ownerAuth].NvDefineSpace(TpmHandle.RhOwner, nvAuth,
                                         new NvPublic(nvHandle, TpmAlgId.Sha1,
                                                      NvAttr.TpmaNvAuthread | NvAttr.TpmaNvAuthwrite,
                                                      new byte[0], 32));

            //
            // Write some data
            // 
            var nvData = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };
            tpm[nvAuth].NvWrite(nvHandle, nvHandle, nvData, 0);

            //
            // And read it back
            // 
            byte[] nvRead = tpm[nvAuth].NvRead(nvHandle, nvHandle, (ushort)nvData.Length, 0);

            //
            // Is it correct?
            // 
            bool correct = nvData.SequenceEqual(nvRead);
            if (!correct)
            {
                throw new Exception("NV data was incorrect.");
            }

            this.textBlock.Text += "NV data written and read. ";
            
            //
            // And clean up
            // 
            tpm[ownerAuth].NvUndefineSpace(TpmHandle.RhOwner, nvHandle);
        }

        /// <summary>
        /// Demonstrate use of NV counters.
        /// </summary>
        /// <param name="tpm">Reference to the TPM object.</param>
        void NVCounter(Tpm2 tpm)
        {
            //
            // AuthValue encapsulates an authorization value: essentially a byte-array.
            // OwnerAuth is the owner authorization value of the TPM-under-test.  We
            // assume that it (and other) auths are set to the default (null) value.
            // If running on a real TPM, which has been provisioned by Windows, this
            // value will be different. An administrator can retrieve the owner
            // authorization value from the registry.
            //
            var ownerAuth = new AuthValue();
            TpmHandle nvHandle = TpmHandle.NV(3001);

            //
            // Clean up any slot that was left over from an earlier run
            // 
            tpm[ownerAuth]._AllowErrors().NvUndefineSpace(TpmHandle.RhOwner, nvHandle);

            //
            // Scenario 2 - A NV-counter
            // 
            AuthValue nvAuth = AuthValue.FromRandom(8);
            tpm[ownerAuth].NvDefineSpace(TpmHandle.RhOwner, nvAuth,
                                         new NvPublic(nvHandle, TpmAlgId.Sha1,
                                                      NvAttr.TpmaNvCounter |
                                                      NvAttr.TpmaNvAuthread |
                                                      NvAttr.TpmaNvAuthwrite,
                                                      new byte[0], 8));
            //
            // Must write before we can read
            // 
            tpm[nvAuth].NvIncrement(nvHandle, nvHandle);

            //
            // Read the current value
            // 
            byte[] nvRead = tpm[nvAuth].NvRead(nvHandle, nvHandle, 8, 0);
            var initVal = Marshaller.FromTpmRepresentation<ulong>(nvRead);

            //
            // Increment
            // 
            tpm[nvAuth].NvIncrement(nvHandle, nvHandle);

            //
            // Read again and see if the answer is what we expect
            // 
            nvRead = tpm[nvAuth].NvRead(nvHandle, nvHandle, 8, 0);
            var finalVal = Marshaller.FromTpmRepresentation<ulong>(nvRead);
            if (finalVal != initVal + 1)
            {
                throw new Exception("NV-counter fail");
            }

            this.textBlock.Text += "Incremented counter from " + initVal.ToString() + " to " + finalVal.ToString() + ". ";
            
            //
            // Clean up
            // 
            tpm[ownerAuth].NvUndefineSpace(TpmHandle.RhOwner, nvHandle);
        }

        private void button_Click(object sender, Windows.UI.Xaml.RoutedEventArgs e)
        {
            try
            {
                Tpm2Device tpmDevice = new TbsDevice();
                tpmDevice.Connect();

                //
                // Pass the device object used for communication to the TPM 2.0 object
                // which provides the command interface.
                // 
                var tpm = new Tpm2(tpmDevice);

                NVReadWrite(tpm);
                NVCounter(tpm);

                tpm.Dispose();
            }
            catch (Exception ex)
            {
                this.textBlock.Text = "Exception occurred: " + ex.Message;
            }
        }
    }
}

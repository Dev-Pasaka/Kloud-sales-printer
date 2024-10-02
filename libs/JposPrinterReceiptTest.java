// JposPrinterReceiptTest.java
// This file contains sample code illustrating how to use the POSPrinter class to
// control your Jpos printer.  The basic printing and status querying mechanisms
// are used here.  For more advanced usage of the POSPrinter class, see the 
// JavaPOS specification.

// usage instructions
// 1. compile from command line - javac -encoding utf-8 -classpath jpos113-controls.jar;jcl.jar JposPrinterReceiptTest.java
// 2. execute from command line - java -classpath .;JposPrinterJavaPOS.jar;jpos113-controls.jar;jcl.jar;xercesimpl.jar;xml-apis.jar JposPrinterReceiptTest

// NOTE: CHANGE THE PRINTER NAME IN THE printer.open STATEMENT BELOW TO MATCH YOUR CONFIGURED DEVICE NAME

import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;

import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import jpos.events.OutputCompleteEvent;
import jpos.events.OutputCompleteListener;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;
import jpos.util.JposPropertiesConst;



public class JposPrinterReceiptTest implements OutputCompleteListener, StatusUpdateListener, ErrorListener {

	public void outputCompleteOccurred(OutputCompleteEvent event) {
		System.out.println("OutputCompleteEvent received: time = "
				+ System.currentTimeMillis() + " output id = "
				+ event.getOutputID());
	}

	public void statusUpdateOccurred(StatusUpdateEvent event) {
		System.out.println("StatusUpdateEvent : status id = " + event.getStatus());
	}

	public void errorOccurred(ErrorEvent event) {
		System.out.println("ErrorEvent received: time = "
				+ System.currentTimeMillis() + " error code = "
				+ event.getErrorCode() + " error code extended = "
				+ event.getErrorCodeExtended());

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}

		event.setErrorResponse(JposConst.JPOS_ER_RETRY);
	}

	public void runTest(String[] args) {
		/*
		 * If you want to place the jpos.xml file elsewhere on your local file
		 * system then uncomment the following line and specify the full path to
		 * jpos.xml.
		 */
		System.setProperty(	JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, "jpos.xml");
		// System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_URL_PROP_NAME, "E:\JavaPOS\JposPrinterJavaPOS\jpos.xml");

		String ESC = ((char) 0x1b) + "";
		String LF = ((char) 0x0a) + "";
		String QR_DOT=((char)0x8)+"";
		String QR_ER_LV=((char)0x30)+"";
		String PDF_PAR=((char)0x00)+""+((char)0x00)+""+((char)0x06)+""+((char)0x03)+""+
				((char)0x30)+""+((char)0x32)+""+((char)0x00)+"";
		String SPACES = "                                                                      ";

		// instantiate a new jpos.POSPrinter object
		POSPrinter printer = new POSPrinter();
		try {
			// register for asynchronous OutputCompleteEvent notification
			printer.addOutputCompleteListener(this);

			// register for asynchronous StatusUpdateEvent notification
			printer.addStatusUpdateListener(this);

			// register for asynchronous ErrorEvent notification
			printer.addErrorListener(this);

			// open the printer object according to the entry names defined in jpos.xml
			//printer.open("JposPrinter_USB");
			//printer.open("JposPrinter_serial");
			printer.open("JposPrinter_USB");

			// claim exclusive usage of the printer object
			printer.claim(1);

			// enable the device for input and output
			printer.setDeviceEnabled(true);

			printer.setAsyncMode(true);

			// set map mode to metric - all dimensions specified in 1/100mm units
			printer.setMapMode(POSPrinterConst.PTR_MM_METRIC); // unit = 1/100 mm - i.e. 1 cm = 10 mm = 10 * 100 units

			do {
				// register for asynchronous StatusUpdateEvent notification
				// see the JavaPOS specification for details on this

				// printer.checkHealth(JposConst.JPOS_CH_EXTERNAL);
				// printer.checkHealth(JposConst.JPOS_CH_INTERACTIVE);

				// check if the cover is open
				if (printer.getCoverOpen() == true) {
					System.out.println("printer.getCoverOpen() == true");

					// cover open so do not attempt printing
					break;
				}

				// check if the printer is out of paper
				if (printer.getRecEmpty() == true) {
					System.out.println("printer.getRecEmpty() == true");

					// the printer is out of paper so do not attempt printing
					break;
				}

				// being a transaction
				// transaction mode causes all output to be buffered
				// once transaction mode is terminated, the buffered data is
				// outputted to the printer in one shot - increased reliability
				printer.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_TRANSACTION);

				if (printer.getCapRecBitmap() == true) {
					// print an image file
					try {
						printer.printBitmap(POSPrinterConst.PTR_S_RECEIPT, "posprinter.jpg", POSPrinterConst.PTR_BM_ASIS, POSPrinterConst.PTR_BM_CENTER);
					} catch (JposException e) {
						if (e.getErrorCode() != JposConst.JPOS_E_NOEXIST) {
							// error other than file not exist - propogate it
							throw e;
						}
						// image file not found - ignore this error & proceed
					}
				}

				// call printNormal repeatedly to generate out receipt the following
				// JavaPOS-POSPrinter control code sequences are used here
				// ESC + "|cA" -> center alignment
				// ESC + "|4C" -> double high double wide character printing
				// ESC + "|bC" -> bold character printing
				// ESC + "|uC" -> underline character printing
				// ESC + "|rA" -> right alignment
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, LF);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|cA" + ESC + "|bC" + "ZhuHai, China, 广东" + LF);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|cA" + ESC + "|bC" + "0756-3895710" + LF);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|uC" + "Qnty Unit Tx Description" +
						SPACES.substring(0, printer.getRecLineChars() - "Qnty Unit Tx Description".length()) + LF);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "   1  100    POS58IIH" + LF);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "   1  200    POS76E" + LF);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "   1  300    POSC2008" + LF);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|rA" + "Subtotal:  600" + LF);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|rA" + "Tax:        50" + LF);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|rA" + ESC + "|bC" + "Total:     650" + LF);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|rA" + "Tender:    700" + LF);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|rA" + ESC + "|bC" + "Change:      50" + LF);
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, LF);

				if (printer.getCapRecBarCode() == true) {
					// print a PDF417 Code
					// the 10 * 100, 60 * 100 parameters below specify the barcode's
					// height and width in the metric map mode (1cm tall, 6cm wide)
					/*printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT,
							ESC+PDF_PAR+"JposPrinter(C)2016 By Lee",
							POSPrinterConst.PTR_BCS_PDF417,
							10 * 100, 60 * 100,
							POSPrinterConst.PTR_BC_CENTER,
							POSPrinterConst.PTR_BC_TEXT_NONE);*/
					printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT,
					ESC+QR_DOT+QR_ER_LV+"JposPrinter(C)2016 By Lee",
					POSPrinterConst.PTR_BCS_QRCODE,
					10 * 100, 60 * 100,
					POSPrinterConst.PTR_BC_CENTER,
				  POSPrinterConst.PTR_BC_TEXT_NONE);
					/*printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT,
							"JposPrinter(C)2016 By Lee",
							POSPrinterConst.PTR_BCS_Code128,
							10 * 100, 60 * 100,
							POSPrinterConst.PTR_BC_CENTER,
							POSPrinterConst.PTR_BC_TEXT_NONE);*/
				}

				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|cA" + ESC + "|4C" + ESC + "|bC" + "Thank you" + LF);

				// the ESC + "|100fP" control code causes the printer to execute
				// a paper cut after feeding to the cutter position
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|100fP");

				// terminate the transaction causing all of the above buffered
				// data to be sent to the printer
				printer.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_NORMAL);

				System.out.println("Async transaction print submited: time = "
						+ System.currentTimeMillis() + " output id = " + printer.getOutputID());

				// exit our printing loop
			} while (false);
		} catch (JposException e) {
			// display any errors that come up
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// close the printer object
			if (printer.getState() != JposConst.JPOS_S_CLOSED) {
				try {
					while (printer.getState() != JposConst.JPOS_S_IDLE) {
						Thread.sleep(0);
					}

					printer.close();
				} catch (Exception e) {
				}
			}
		}

		System.out.println("JposPrinterReceiptTest finished.");
	}

	public static void main(String[] args) {
		new JposPrinterReceiptTest().runTest(args);
	}
}

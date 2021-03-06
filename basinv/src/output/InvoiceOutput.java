package output;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;

import model.Address;
import model.Business;
import model.Customer;
import model.Invoice;
import model.InvoiceDetail;
import persistency.RDBConnection;
import persistency.controller.AddressController;
import persistency.controller.CodeController;
import persistency.controller.CustomerController;
import persistency.controller.InvoiceController;
import persistency.controller.InvoiceDetailController;
import persistency.controller.ProductController;
import utilities.Constants;
import utilities.CreateDirectory;
import utilities.Figures;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class InvoiceOutput extends DocumentOutput {
	private Invoice invoice;

	private Iterator<Business> details;

	private Customer customer;

	/** The resulting PDF file. */
	private final String TITLE;
	private final String PREFIX;

	public InvoiceOutput(String id) {
		this(InvoiceController.getInvoice(id));
	}

	public InvoiceOutput(Invoice invoice) {
		this.invoice = invoice;
		this.details = InvoiceDetailController.readInvoiceDetails(
				invoice.getIdInvoice()).iterator();
		setVat(invoice.isInvVat());
		TITLE = (invoice.getInvType().equals(Constants.INVOICE_TYPE) ? Constants.INVOICE
				: Constants.CREDIT_NOTE);
		PREFIX = (invoice.getInvType().equals(Constants.INVOICE_TYPE) ? Constants.INVOICE
				: Constants.CREDIT_NOTE);
	}

	/**
	 * Invoice Header
	 * 
	 * @return Table
	 */
	public PdfPTable createCustomerInfo() {
		// a table with three columns
		PdfPTable table = new PdfPTable(10);
		// the cell object
		PdfPCell cell = null;
		Address address = AddressController.getAddress(invoice.getInvAddid());
		// 10 cells per row
		// row 1
		cell = new PdfPCell(new Phrase(Constants.BLANK));
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setColspan(1);
		table.addCell(cell);

		if (!getHead().getCusMobile().equals("")) {
			cell = new PdfPCell(new Phrase("GSM: " + getHead().getCusMobile(),
					FONT[8]));
		} else {
			cell = new PdfPCell(new Phrase(Constants.BLANK));
		}
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setColspan(4);
		table.addCell(cell);


		cell = new PdfPCell(new Phrase(customer.getCusName(), FONT[17]));
		cell.setColspan(5);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		// row 2
		cell = new PdfPCell(new Phrase(Constants.BLANK));
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setColspan(1);
		table.addCell(cell);

		if (!getHead().getCusVat().equals("")) {
			cell = new PdfPCell(new Phrase("BTW: " + getHead().getCusVat(),
					FONT[8]));
		} else {
			cell = new PdfPCell(new Phrase(Constants.BLANK));
		}
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setColspan(2);
		table.addCell(cell);

		if (!getHead().getCusInfo().equals("")) {
			cell = new PdfPCell(new Phrase("Hr." + getHead().getCusInfo(),
					FONT[8]));
		} else {
			cell = new PdfPCell(new Phrase(Constants.BLANK));
		}
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setColspan(2);
		table.addCell(cell);


		cell = new PdfPCell(new Phrase(address.getAddStreet()
				+ Constants.BLANK
				+ address.getAddNumber()
				+ (address.getAddBox().equals("") ? "" : " bus "
						+ address.getAddBox()), FONT[17]));
		cell.setColspan(5);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		// row 3
		cell = new PdfPCell(new Phrase(getHead().getCusName(), FONT[1]));
		cell.setColspan(5);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);


		cell = new PdfPCell(new Phrase(address.getAddZip()
				+ Constants.BLANK
				+ address.getAddCity()
				+ Constants.PERIOD
				+ CodeController.getOneCodeDetail(Constants.COUNTRY_CODE,
						address.getAddCountry()).getCodeDesc(), FONT[17]));
		cell.setColspan(5);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		// row 4
		cell = new PdfPCell(new Phrase("Factuurdatum", FONT[18]));
		cell.setColspan(2);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(invoice.getInvDate()
				.getDatumInEuropeesFormaat(), FONT[20]));
		cell.setColspan(8);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		// row 5
		cell = new PdfPCell(new Phrase("Vervaldatum", FONT[18]));
		cell.setColspan(2);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(invoice.getInvDueDate()
				.getDatumInEuropeesFormaat(), FONT[20]));
		cell.setColspan(8);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		// row 6
		cell = new PdfPCell(new Phrase("Referentie", FONT[18]));
		cell.setColspan(2);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		cell = new PdfPCell(
				new Phrase(invoice.getInvHeaderComments(), FONT[20]));
		cell.setColspan(3);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(Constants.BTW, FONT[18]));
		cell.setColspan(1);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(customer.getCusVat(), FONT[20]));
		cell.setColspan(4);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		// row7 (blank line)
		cell = new PdfPCell(new Phrase(Constants.BLANK));
		cell.setColspan(10);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		return table;
	}

	/**
	 * @param table
	 * @param lineCounter
	 * @return
	 */
	public int createOneDetailLine(PdfPTable table, int lineCounter) {
		PdfPCell cell;
		InvoiceDetail detail;
		BigDecimal lineTotal;
		while (details.hasNext()) {
			detail = (InvoiceDetail) details.next();

			cell = new PdfPCell(new Phrase((detail.getInvProdid()!=null)?ProductController.getProduct(
					detail.getInvProdid()).getProdDesc():detail.getInvComments(), FONT[Integer.parseInt(RDBConnection.getProps().getProperty(Constants.COMMENT_FONT_SIZE))]));
			cell.setColspan(14);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase(((detail.getInvQty().doubleValue()!= Figures.ZERO)?detail.getInvQty().toString():Constants.BLANK),
					FONT[11]));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase(detail.getInvMeasure(), FONT[11]));
			table.addCell(cell);

//			cell = new PdfPCell(new Phrase(((detail.getInvQty().doubleValue()!= Figures.ZERO)?detail.getInvQty().toString():Constants.BLANK),
//					FONT[11]));
//			cell.setColspan(2);
//			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//			table.addCell(cell);

			cell = new PdfPCell(new Phrase(((!detail.getInvVat().equals(Constants._ZERO)
					)? detail.getInvVat().toString()
					+ Constants.PERCENTAGE:Constants.BLANK), FONT[11]));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase(((detail.getInvPrice().doubleValue()!= Figures.ZERO)? detail.getInvPrice().toString()
					+ Constants.EURO:Constants.BLANK), FONT[11]));
			cell.setColspan(3);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			// calculate totals
			lineTotal = getInvoiceLineTotal(detail).setScale(2,
					BigDecimal.ROUND_HALF_UP);
			cell = new PdfPCell(new Phrase(((lineTotal.doubleValue()!= Figures.ZERO)?lineTotal.toString()
					+ Constants.EURO:Constants.BLANK), FONT[18]));
			cell.setColspan(3);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			lineCounter--;
			if (!detail.getInvComments().equals(Constants.EMPTY)&& (detail.getInvProdid()!=null)) {
				lineComments(detail, table);
				lineCounter--;
			}
		}
		return lineCounter;
	}

	private BigDecimal getInvoiceLineTotal(InvoiceDetail detail) {

		BigDecimal total = detail.getInvQty().multiply(detail.getInvPrice());
		setTotalExcl(getTotalExcl() + total.doubleValue());
		if (isVat()) {
			BigDecimal vat = new BigDecimal(detail.getInvVat());
			double vatAmount = Figures.ZERO;
			vatAmount = total.doubleValue() * vat.doubleValue()
					/ Figures.HUNDRED;
			if (vat.doubleValue() == Figures.SIX) {
				setTotalVat6(getTotalVat6() + vatAmount);
			}
			if (vat.doubleValue() == Figures.TWENTYONE) {
				setTotalVat21(getTotalVat21() + vatAmount);
			}
		}
		return total;
	}

	/**
	 * Subcontractor not VAT obliged
	 * 
	 * @param table
	 */
	public void lineComments(InvoiceDetail detail, PdfPTable table) {
		PdfPCell cell;
		cell = new PdfPCell(
				new Phrase("\t" + detail.getInvComments(), FONT[10]));
		cell.setColspan(22);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(Constants.BLANK));
		cell.setColspan(3);
		table.addCell(cell);
	}

	/**
	 * @return table
	 */
	public PdfPTable lineNoVat() {
		PdfPTable table = new PdfPTable(25);
		if (!isVat()) {
			// subcontractor
			noVatContractor(table);
		}

		return table;

	}

	public void createPdf(String filename) throws IOException,
			DocumentException {
		// step 1
		Document document = new Document();
		// step 2
		PdfWriter.getInstance(document, new FileOutputStream(filename));
		// step 3
		document.open();
		// step 4
		document.add(createHeader(TITLE, invoice.getIdInvoice()));
		document.add(createCustomerInfo());
		document.add(createDetails());
		// no VAT text
		document.add(lineNoVat());
		document.add(createFooter());

		// step 5
		document.close();
	}

	public void run() {
		String strManyDirectories = Constants.DOCUMENT_PATH + Constants.INVOICE_DETAIL_PATH;
		try {
			CreateDirectory.run(strManyDirectories);
			customer = CustomerController.getCustomer(invoice.getInvCusid());
			createPdf(strManyDirectories + PREFIX + Constants.SEPARATOR_FLAT
					+ invoice.getIdInvoice() + Constants.SEPARATOR_FLAT
					+ customer.getCusName()  + Constants.EXTENTION);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

}

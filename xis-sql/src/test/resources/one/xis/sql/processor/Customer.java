package one.xis.sql.processor;

import one.xis.sql.Entity;
import one.xis.sql.ForeignKey;
import one.xis.sql.Id;

@Entity
class Customer {

    @Id
    private Long id;


    private String firstName;
    private String lastName;

    @ForeignKey(columnName = "address_id")
    private InvoiceAddress invoiceAddress;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InvoiceAddress getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(InvoiceAddress invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }
}
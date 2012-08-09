package com.maximdim.conconj.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Contact", namespace = "http://ws.constantcontact.com/ns/1.0/")
@XmlAccessorType(XmlAccessType.NONE)
public class Contact {

  @XmlAttribute
  public String id;

  @XmlElement(name="EmailAddress")
  public String emailAddress;
  
  /**
   * Usual value is 'ACTION_BY_CUSTOMER'
   */
  @XmlElement(name="OptInSource")
  public OptInSource optInSource = OptInSource.ACTION_BY_CUSTOMER;
  
  @XmlElement(name="FirstName")
  public String firstName;
  
  @XmlElement(name="LastName")
  public String lastName;
  
  @XmlElement(name="CustomField1")
  public String customField1;
  
  @XmlElement(name="ContactLists")
  public ContactLists contactLists = new ContactLists();

  public Contact() {
    // default constructor, required by JAXB
  }
  
  public Contact(String emailAddress) {
    this.emailAddress = emailAddress;
  }
  
  public String getShortId() {
    return toShortId(this.id);
  }
  
  public void addList(String listId) {
    this.contactLists.contactLists.add(new ContactList(listId));
  }
  
  public boolean hasList(String listId) {
    for(ContactList cl : this.contactLists.contactLists) {
      if (cl.id.equals(listId)) {
        return true;
      }
    }
    return false;
  }
  
  public static String toShortId(String fullId) {
    return fullId != null ? fullId.substring(fullId.lastIndexOf('/')+1) : null;
  }
  
}

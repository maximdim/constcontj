package com.maximdim.conconj.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ContactList", namespace = "http://ws.constantcontact.com/ns/1.0/")
@XmlAccessorType(XmlAccessType.NONE)
public class ContactList {
  
  @XmlAttribute
  public String id;

  @XmlElement(name="OptInSource")
  public OptInSource optInSource = OptInSource.ACTION_BY_CUSTOMER;

  @XmlElement(name="Name")
  public String name;
  
  @XmlElement(name="ShortName")
  public String shortName;
  
  public ContactList() {
    // default constructor required by JAXB
  }
  
  public ContactList(String id) {
    this.id = id;
  }


}

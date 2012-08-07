package com.maximdim.conconj.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * TODO: Perhaps it's not necessary to have this intermediate class just to hold list of ContactLists
 *
 */
@XmlRootElement(name = "ContactLists", namespace = "http://ws.constantcontact.com/ns/1.0/")
@XmlAccessorType(XmlAccessType.NONE)
public class ContactLists {

  @XmlElementRef
  public List<ContactList> contactLists = new ArrayList<ContactList>();

}

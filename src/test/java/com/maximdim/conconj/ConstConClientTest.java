package com.maximdim.conconj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.maximdim.conconj.model.Contact;
import com.maximdim.conconj.model.ContactList;

public class ConstConClientTest {
  private static ConstConClient client;
  private static String contactId;
  private static String contactEmail;
  private static String contactListId;
  
  @BeforeClass
  public static void init() throws Exception {
    Properties p = new Properties();
    p.load(new FileReader(new File(System.getProperty("user.home"), "constconj.properties")));
    
    client = new ConstConClient(p.getProperty("API_KEY"), p.getProperty("USERNAME"), p.getProperty("PASSWORD"));

    List<ContactList> lists = client.getLists(false);
    assertFalse("Need to have at leats one list created", lists.isEmpty());
    contactListId = lists.get(0).id;
    
    // create new contact with random email
    contactEmail = "test"+System.currentTimeMillis()+"@test.com";
    Contact contact = new Contact(contactEmail);
    contact.addList(contactListId);
    
    contactId = client.createContact(contact);
  }
  
  @AfterClass
  public static void destroy() {
    client.optOutContact(contactId);
    client.shutdown();
  }
  
  @Test
  public void testGetLists() {
    List<ContactList> listsAll = client.getLists(true);
    assertFalse(listsAll.isEmpty());
    
    List<ContactList> lists = client.getLists(false);
    assertFalse(lists.isEmpty());
    
    assertTrue(listsAll.size() >= lists.size());
  }
  
  @Test
  public void testGetContactById() {
    Contact contact = client.getContactById(contactId);
    assertEquals(contactId, contact.getShortId());
  }

  @Test
  public void testSearchContactByEmail() {
    List<Contact> contacts = client.searchContactByEmail(contactEmail);
    assertEquals(1, contacts.size());
    
    Contact contact = contacts.get(0);
    assertEquals(contactId, contact.getShortId());
  }

  @Test
  public void testSignupToList() {
    Contact contact = client.getContactById(contactId);
    
    String listId = null;
    for(ContactList cl : client.getLists(false)) {
      if (!contactListId.equals(cl.id)) {
        listId = cl.id;
        break;
      }
    }
    assertNotNull("No lists available to signup to", listId);
    assertFalse(contact.hasList(listId));
    
    client.addContactToList(contact.getShortId(), listId);
    
    contact = client.getContactById(contactId);
    assertTrue(contact.hasList(listId));
  }
  
}

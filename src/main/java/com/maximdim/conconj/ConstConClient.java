package com.maximdim.conconj;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Person;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.maximdim.conconj.model.Contact;
import com.maximdim.conconj.model.ContactList;

/**
 * High level client for Constant Contact
 * 
 * @author maxim@maximdim.com
 * 
 */
public class ConstConClient {
  private static final Log logger = LogFactory.getLog(ConstConClient.class);

  private static final String API_URL = "https://api.constantcontact.com";
  private final String API_KEY, USER, PASSWORD;

  private final DefaultHttpClient httpClient;
  private final ConstConApi api;

  public ConstConClient(String apiKey, String user, String password) {
    API_KEY = apiKey;
    USER = user;
    PASSWORD = password;

    RegisterBuiltin.register(ResteasyProviderFactory.getInstance());

    this.httpClient = new DefaultHttpClient();
    this.httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY,
        new UsernamePasswordCredentials(API_KEY + "%" + USER, PASSWORD));

    this.api = ProxyFactory.create(ConstConApi.class, API_URL,
        new ApacheHttpClient4Executor(httpClient));
  }

  public void shutdown() {
    this.httpClient.getConnectionManager().shutdown();
  }

  private static final Set<String> SYSTEM_LISTS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(new String[]{"Do Not Mail", "Active", "Removed"})));
  
  /**
   * @param includeAll if true then return all lists defined, including special system lists
   */
  public List<ContactList> getLists(boolean includeAll) {
    List<ContactList> result = new ArrayList<ContactList>();
    Feed feed = this.api.getLists(USER);
    
    logger.debug("Found lists: "+feed.getEntries().size());
    try {
      for(Entry e: feed.getEntries()) {
        ContactList list = e.getContent().getJAXBObject(ContactList.class);
        if (!includeAll && SYSTEM_LISTS.contains(list.shortName)) {
          continue;
        }
        result.add(list);
      }
    } 
    catch (JAXBException e) {
      throw new ConstConClientException(e.getMessage(), e);
    }
    return result;
  }
  
  public Contact getContactById(String contactId) {
    logger.info("getContactById [" + contactId +"]");
    Entry contactEntry = this.api.getContact(USER, contactId);
    Content content = contactEntry.getContent();
    try {
      return content.getJAXBObject(Contact.class);
    }
    catch (JAXBException e) {
      throw new ConstConClientException(e.getMessage(), e);
    }
  }

  public List<Contact> searchContactByEmail(String email) {
    logger.info("searchContactByEmail [" + email +"]");
    List<Contact> result = new ArrayList<Contact>();
    
    Feed feed = this.api.searchByEmail(USER, email);
    logger.debug("Found: "+feed.getEntries().size());
    
    for(Entry e: feed.getEntries()) {
      String id = Contact.toShortId(e.getId().toString());
      result.add(getContactById(id));
    }
    return result;
  }
  
  /**
   * It seesm that Contact should have at least one ContactList defined in order to be created successfully
   */
  public String createContact(Contact contact) {
    try {
      Entry entry = new Entry();
      // looks like CC requires to have bunch of extra field set even so they're not used
      entry.setId(new URI("data:,none"));
      entry.setSummary("Contact");
      entry.setTitle(" ");
      entry.setUpdated(new Date());
      entry.getAuthors().add(new Person(" "));
      Content content = new Content();
      content.setType(new MediaType("application", "vnd.ctct+xml"));
      content.setJAXBObject(contact);
      entry.setContent(content);
      
      logger.debug(asString(entry));
      
      Entry result = this.api.createContact(USER, entry);
      
      String contactId = result.getContent().getJAXBObject(Contact.class).getShortId();
      logger.info("Contact created ["+contactId+"]");

      return contactId;
    }
    catch (JAXBException e) {
      throw new ConstConClientException(e.getMessage(), e);
    } 
    catch (URISyntaxException e) {
      throw new ConstConClientException(e.getMessage(), e);
    }
    catch (ClientResponseFailure e) {
      // ClientResponseFailure thrown with status 409 ('Conflict')
      // returned if contact already exist
      if (e.getResponse().getStatus() == 409) {
        throw new ConstConClientException("Contact already exist", e);
      }
      else {
        throw e;
      }
    }
  }

  public void optOutContact(String contactId) {
    logger.info("optOutContact ["+contactId+"]");
    this.api.optOutContact(USER, contactId);
  }
  
  public void addContactToList(String contactId, String listId) {
    logger.info("addContactToList ["+contactId+"] ["+listId+"]");
    
    try {
      Entry entry = this.api.getContact(USER, contactId);
      Contact c = entry.getContent().getJAXBObject(Contact.class);
      if (c.hasList(listId)) {
        logger.warn("Contact ["+contactId+"] already in list ["+listId+"], Noop" );
        return;
      }
      c.addList(listId);
      entry.getContent().setJAXBObject(c);
      
      this.api.updateContact(USER, c.getShortId(), entry);
    }
    catch (JAXBException e) {
      throw new ConstConClientException(e.getMessage(), e);
    } 
    
  }
  
  private static String asString(Object o) throws JAXBException {
    JAXBContext jc = JAXBContext.newInstance(Feed.class, Contact.class);
    Marshaller marshaller = jc.createMarshaller();
    StringWriter sw = new StringWriter();
    marshaller.marshal(o, sw);
    return sw.toString();
  }

}

package com.maximdim.conconj;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;

/**
 * Low level client of Constant Contact API
 * 
 * @author maxim@maximdim.com
 *
 */
public interface ConstConApi {
  
  /**
   * Search Contacts by email
   * http://community.constantcontact.com/t5/Documentation/Searching-for-a-Contact-by-Email-Address/ba-p/25123
   */
  @GET
  @Path("ws/customers/{username}/contacts")
  @Produces("application/atom+xml")
  Feed searchByEmail(@PathParam("username") String user, @QueryParam("email") String email);
  
  
  /**
   * Get Contact info
   * http://community.constantcontact.com/t5/Documentation/Obtaining-a-Contact-s-Information/ba-p/25057
   */
  @GET
  @Path("ws/customers/{username}/contacts/{contactId}")
  @Produces("application/atom+xml")
  Entry getContact(@PathParam("username") String user, @PathParam("contactId") String contactId);
  
  /**
   * Create new contact
   * http://community.constantcontact.com/t5/Documentation/Creating-a-Contact/ba-p/25059
   */
  @POST
  @Path("ws/customers/{username}/contacts")
  @Consumes("application/atom+xml")
  @Produces("application/atom+xml")
  Entry createContact(@PathParam("username") String user, Entry entry);
  
  /**
   * Update contact. To subscribe contact to new list add ContactList entry to ContactLists
   * http://community.constantcontact.com/t5/Documentation/Adding-a-Contact-to-a-List/ba-p/25121
   */
  @PUT
  @Path("ws/customers/{username}/contacts/{contactId}")
  @Consumes("application/atom+xml")
  @Produces("application/atom+xml")
  String updateContact(@PathParam("username") String user, @PathParam("contactId") String contactId, Entry entry);
  
  
  /**
   * Get lists
   */
  @GET
  @Path("ws/customers/{username}/lists")
  @Produces("application/atom+xml")
  Feed getLists(@PathParam("username") String user);
  
  /**
   * Opt-out contact
   * The call will return 204 No Content if it succeeds. 
   * This should be used if the contact has decided to unsubscribe from receiving all emails or has asked to stop sending all emails.  
   * Opted-out contacts become members of the Do-Not-Mail special list and is removed from all other contact lists.
   * 
   * http://community.constantcontact.com/t5/Documentation/Opting-out-a-Contact/ba-p/25117
   */
  @DELETE
  @Path("ws/customers/{username}/contacts/{contactId}")
  String optOutContact(@PathParam("username") String user, @PathParam("contactId") String contactId);
  
}

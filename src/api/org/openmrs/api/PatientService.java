package org.openmrs.api;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.Tribe;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOContext;
import org.openmrs.api.db.PatientDAO;
import org.openmrs.util.OpenmrsConstants;

/**
 * Patient-related services
 * 
 * @author Ben Wolfe
 * @author Burke Mamlin
 * @vesrion 1.0
 */
public class PatientService {

	private Log log = LogFactory.getLog(this.getClass());
	
	private Context context;
	private DAOContext daoContext;
	
	public PatientService(Context c, DAOContext d) {
		this.context = c;
		this.daoContext = d;
	}
	
	private PatientDAO getPatientDAO() {
		return daoContext.getPatientDAO();
	}
	
	/**
	 * Creates a new patient record
	 * 
	 * @param patient to be created
	 * @throws APIException
	 */
	public void createPatient(Patient patient) throws APIException {
		String s = patient.getPatientId().toString();
		if (patient.getIdentifiers() != null)
			s += "|" + patient.getIdentifiers();
		
		log.info(s);
		
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_ADD_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_ADD_PATIENTS);
		getPatientDAO().createPatient(patient);
	}

	/**
	 * Get patient by internal identifier
	 * 
	 * @param patientId internal patient identifier
	 * @return patient with given internal identifier
	 * @throws APIException
	 */
	public Patient getPatient(Integer patientId) throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_VIEW_PATIENTS);
		return getPatientDAO().getPatient(patientId);
	}

	/**
	 * Update patient 
	 * 
	 * @param patient to be updated
	 * @throws APIException
	 */
	public void updatePatient(Patient patient) throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_EDIT_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_EDIT_PATIENTS);
		getPatientDAO().updatePatient(patient);
	}

	/**
	 * Find all patients with a given identifier
	 * 
	 * @param identifier
	 * @return set of patients matching identifier
	 * @throws APIException
	 */
	public Set<Patient> getPatientsByIdentifier(String identifier, boolean includeVoided) throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_VIEW_PATIENTS);
		return getPatientDAO().getPatientsByIdentifier(identifier, includeVoided);
	}
	
	/**
	 * Find all patients with a given identifier and use the regex 
	 * <code>OpenmrsConstants.PATIENT_IDENTIFIER_REGEX</code>
	 * 
	 * Note: Uses NON-STANDARD SQL: "...WHERE identifier REGEXP '...' ..."
	 * 
	 * @param identifier
	 * @return set of patients matching identifier
	 * @throws APIException
	 */
	public Set<Patient> getPatientsByIdentifierPattern(String identifier, boolean includeVoided) throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_VIEW_PATIENTS);
		return getPatientDAO().getPatientsByIdentifierPattern(identifier, includeVoided);
	}
	
	
	public Set<Patient> getPatientsByName(String name) throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_VIEW_PATIENTS);
		return getPatientsByName(name, false);
	}
	/**
	 * Find patients by name
	 * 
	 * @param name
	 * @return set of patients matching name
	 * @throws APIException
	 */
	public Set<Patient> getPatientsByName(String name, boolean includeVoided) throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_VIEW_PATIENTS);
		return getPatientDAO().getPatientsByName(name, includeVoided);
	}
	
	
	public Set<Patient> getSimilarPatients(String name, Integer birthyear, String gender) throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_VIEW_PATIENTS);
		return getPatientDAO().getSimilarPatients(name, birthyear, gender);
	}
	
	
	/**
	 * Void patient record (functionally delete patient from system)
	 * 
	 * @param patient patient to be voided
	 * @param reason reason for voiding patient
	 */
	public void voidPatient(Patient patient, String reason) throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_EDIT_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_EDIT_PATIENTS);
		getPatientDAO().voidPatient(patient, reason);
	}

	/**
	 * Unvoid patient record 
	 * 
	 * @param patient patient to be revived
	 */
	public void unvoidPatient(Patient patient) throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_EDIT_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_EDIT_PATIENTS);
		getPatientDAO().unvoidPatient(patient);
	}
	
	/**
	 * Delete patient from database. This <b>should not be called</b>
	 * except for testing and administration purposes.  Use the void
	 * method instead.
	 * 
	 * @param patient patient to be deleted
	 * 
	 * @see #voidPatient(Patient, String) 
	 */
	public void deletePatient(Patient patient) throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_DELETE_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_DELETE_PATIENTS);
		getPatientDAO().deletePatient(patient);
	}
	
	/**
	 * Get all patientIdentifiers 
	 * 
	 * @param pit
	 * @return patientIdentifier list
	 * @throws APIException
	 */
	public List<PatientIdentifier> getPatientIdentifiers(PatientIdentifierType pit) throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_VIEW_PATIENTS);
		
		return getPatientDAO().getPatientIdentifiers(pit);
	}
	
	/**
	 * Update patient identifier
	 * 
	 * @param patient to be updated
	 * @throws APIException
	 */
	public void updatePatientIdentifier(PatientIdentifier pi) throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_EDIT_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_EDIT_PATIENTS);
		getPatientDAO().updatePatientIdentifier(pi);
	}
	
	/**
	 * Get all patientIdentifier types
	 * 
	 * @return patientIdentifier types list
	 * @throws APIException
	 */
	public List<PatientIdentifierType> getPatientIdentifierTypes() throws APIException {
		if (!context.isAuthenticated())
			throw new APIAuthenticationException("Authentication required");
		
		return getPatientDAO().getPatientIdentifierTypes();
	}

	/**
	 * Get patientIdentifierType by internal identifier
	 * 
	 * @param patientIdentifierType id
	 * @return patientIdentifierType with given internal identifier
	 * @throws APIException
	 */
	public PatientIdentifierType getPatientIdentifierType(Integer patientIdentifierTypeId) throws APIException {
		if (!context.isAuthenticated())
			throw new APIAuthenticationException("Authentication required");
		
		return getPatientDAO().getPatientIdentifierType(patientIdentifierTypeId);
	}

	/**
	 * Get tribe by internal tribe identifier
	 * 
	 * @return Tribe
	 * @param tribeId 
	 * @throws APIException
	 */
	public Tribe getTribe(Integer tribeId) throws APIException {
		if (!context.isAuthenticated())
			throw new APIAuthenticationException("Authentication required");
		
		return getPatientDAO().getTribe(tribeId);
	}
	
	/**
	 * Get list of tribes that are not retired
	 * 
	 * @return non-retired Tribe list
	 * @throws APIException
	 */
	public List<Tribe> getTribes() throws APIException {
		if (!context.isAuthenticated())
			throw new APIAuthenticationException("Authentication required");
		
		return getPatientDAO().getTribes();
	}
	
	/**
	 * Find tribes by partial name lookup
	 * 
	 * @return non-retired Tribe list
	 * @throws APIException
	 */
	public List<Tribe> findTribes(String search) throws APIException {
		if (!context.isAuthenticated())
			throw new APIAuthenticationException("Authentication required");
		
		return getPatientDAO().findTribes(search);
	}
	
	/**
	 * Get relationship by internal relationship identifier
	 * 
	 * @return Relationship
	 * @param relationshipId 
	 * @throws APIException
	 */
	public Relationship getRelationship(Integer relationshipId) throws APIException {
		if (!context.isAuthenticated())
			throw new APIAuthenticationException("Authentication required");
		
		return getPatientDAO().getRelationship(relationshipId);
	}
	
	/**
	 * Get list of relationships that are not retired
	 * 
	 * @return non-voided Relationship list
	 * @throws APIException
	 */
	public List<Relationship> getRelationships() throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_MANAGE_RELATIONSHIPS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_MANAGE_RELATIONSHIPS);
		
		return getPatientDAO().getRelationships();
	}
	
	/**
	 * Get list of relationships that include Person in person_id or relative_id
	 * 
	 * @return Relationship list
	 * @throws APIException
	 */
	public List<Relationship> getRelationships(Person p) throws APIException {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_MANAGE_RELATIONSHIPS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_MANAGE_RELATIONSHIPS);
		
		return getPatientDAO().getRelationships(p);
	}
	
	/**
	 * Get all relationshipTypes
	 * 
	 * @return relationshipType list
	 * @throws APIException
	 */
	public List<RelationshipType> getRelationshipTypes() throws APIException {
		if (!context.isAuthenticated())
			throw new APIAuthenticationException("Authentication required");
		
		return getPatientDAO().getRelationshipTypes();
	}

	/**
	 * Get relationshipType by internal identifier
	 * 
	 * @param relationshipType id
	 * @return relationshipType with given internal identifier
	 * @throws APIException
	 */
	public RelationshipType getRelationshipType(Integer relationshipTypeId) throws APIException {
		// TODO use 'Authenticated User' option
		if (!context.isAuthenticated())
			throw new APIAuthenticationException("Authentication required");
		
		return getPatientDAO().getRelationshipType(relationshipTypeId);
	}
	
	/**
	 * Get all locations
	 * 
	 * @return location list
	 * @throws APIException
	 */
	public List<Location> getLocations() throws APIException {
		if (!context.isAuthenticated())
			throw new APIAuthenticationException("Authentication required");
		
		return getPatientDAO().getLocations();
	}

	/**
	 * Get location by internal identifier
	 * 
	 * @param location id
	 * @return location with given internal identifier
	 * @throws APIException
	 */
	public Location getLocation(Integer locationId) throws APIException {
		// TODO use 'Authenticated User' option
		if (!context.isAuthenticated())
			throw new APIAuthenticationException("Authentication required");
		
		return getPatientDAO().getLocation(locationId);
	}
	
	public List<Patient> findPatients(String query, boolean includeVoided) {
		if (!context.hasPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS))
			throw new APIAuthenticationException("Privilege required: " + OpenmrsConstants.PRIV_VIEW_PATIENTS);
		
		List<Patient> patients = new Vector<Patient>();
		PatientDAO dao = getPatientDAO();
		
		//query must be more than 2 characters
		if (query.length() < 3)
			return patients;
		
		// if there is a number in the query string
		if (query.matches(".*\\d+.*")) {
			log.debug("[Identifier search] Query: " + query);
			patients.addAll(dao.getPatientsByIdentifierPattern(query, includeVoided));
		}
		else {
			//there is no number in the string, search on name
			patients.addAll(dao.getPatientsByName(query, includeVoided));
		}
		return patients;
	}	
}

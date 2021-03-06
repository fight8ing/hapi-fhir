package org.hl7.fhir.r4.hapi.validation;

import ca.uhn.fhir.context.FhirContext;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.hl7.fhir.r4.hapi.ctx.IValidationSupport;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.ValueSet;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.terminologies.ValueSetExpander;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unchecked")
public class CachingValidationSupport implements IValidationSupport {

	private final IValidationSupport myWrap;
	private final Cache<String, Object> myCache;

	public CachingValidationSupport(IValidationSupport theWrap) {
		myWrap = theWrap;
		myCache = Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).build();
	}

	@Override
	public ValueSetExpander.ValueSetExpansionOutcome expandValueSet(FhirContext theContext, ValueSet.ConceptSetComponent theInclude) {
		return myWrap.expandValueSet(theContext, theInclude);
	}

	@Override
	public List<IBaseResource> fetchAllConformanceResources(FhirContext theContext) {
		return (List<IBaseResource>) myCache.get("fetchAllConformanceResources",
			t -> myWrap.fetchAllConformanceResources(theContext));
	}

	@Override
	public List<StructureDefinition> fetchAllStructureDefinitions(FhirContext theContext) {
		return (List<StructureDefinition>) myCache.get("fetchAllStructureDefinitions",
			t -> myWrap.fetchAllStructureDefinitions(theContext));
	}

	@Override
	public CodeSystem fetchCodeSystem(FhirContext theContext, String uri) {
		return myWrap.fetchCodeSystem(theContext, uri);
	}

	@Override
	public ValueSet fetchValueSet(FhirContext theContext, String uri) {
		return myWrap.fetchValueSet(theContext, uri);
	}

	@Override
	public <T extends IBaseResource> T fetchResource(FhirContext theContext, Class<T> theClass, String theUri) {
		return myWrap.fetchResource(theContext, theClass, theUri);
	}

	@Override
	public StructureDefinition fetchStructureDefinition(FhirContext theCtx, String theUrl) {
		return myWrap.fetchStructureDefinition(theCtx, theUrl);
	}

	@Override
	public boolean isCodeSystemSupported(FhirContext theContext, String theSystem) {
		return myWrap.isCodeSystemSupported(theContext, theSystem);
	}

	@Override
	public CodeValidationResult validateCode(FhirContext theContext, String theCodeSystem, String theCode, String theDisplay) {
		return myWrap.validateCode(theContext, theCodeSystem, theCode, theDisplay);
	}
}

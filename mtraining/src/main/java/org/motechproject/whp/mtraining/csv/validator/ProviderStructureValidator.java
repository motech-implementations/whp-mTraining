package org.motechproject.whp.mtraining.csv.validator;

import org.motechproject.whp.mtraining.csv.domain.CsvImportError;
import org.motechproject.whp.mtraining.csv.request.ProviderCsvRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNumeric;
import static org.motechproject.whp.mtraining.web.domain.ActivationStatus.from;

@Component
public class ProviderStructureValidator {

    public List<CsvImportError> validate(List<ProviderCsvRequest> providerCsvRequests) {
        ArrayList<CsvImportError> errors = new ArrayList<>();
        Set<String> remedyIds = new HashSet<>();
        Set<String> contactNumbers = new HashSet<>();
        for (ProviderCsvRequest providerCsvRequest : providerCsvRequests) {
            String remedyId = providerCsvRequest.getRemedyId();
            validateRemedyId(errors, providerCsvRequest, remedyIds);
            validatePrimaryContactNumber(errors, providerCsvRequest, contactNumbers);

            if (from(providerCsvRequest.getActivationStatus()) == null)
                errors.add(new CsvImportError("Activation Status", "-", "Activation Status for Remedy Id: " + remedyId + " is blank or invalid."));

            if (isBlank(providerCsvRequest.getBlock()))
                errors.add(new CsvImportError("BLock", "-", "Block is blank for Remedy Id: " + remedyId + "."));

            if (isBlank(providerCsvRequest.getDistrict()))
                errors.add(new CsvImportError("BLock", "-", "District is blank for Remedy Id: " + remedyId + "."));

            if (isBlank(providerCsvRequest.getState()))
                errors.add(new CsvImportError("BLock", "-", "State is blank for Remedy Id: " + remedyId + "."));

        }
        return errors;
    }

    private void validatePrimaryContactNumber(ArrayList<CsvImportError> errors, ProviderCsvRequest providerCsvRequest, Set<String> contactNumbers) {
        String primaryContactNumber = providerCsvRequest.getPrimaryContactNumber();
        if (contactNumbers.contains(primaryContactNumber))
            errors.add(new CsvImportError("Primary Contact has multiple occurrences."));
        if (isBlank(primaryContactNumber) || !isNumeric(primaryContactNumber) || primaryContactNumber.length() != 10) {
            errors.add(new CsvImportError("Primary Contact Number", "-", "Primary Contact Number is invalid for Remedy Id: " + providerCsvRequest.getRemedyId() + ". It should be a 10 digit phone number."));
            return;
        }
        contactNumbers.add(primaryContactNumber);
    }

    private void validateRemedyId(ArrayList<CsvImportError> errors, ProviderCsvRequest providerCsvRequest, Set<String> remedyIds) {
        String remedyId = providerCsvRequest.getRemedyId();
        if (remedyIds.contains(remedyId))
            errors.add(new CsvImportError("RemedyId", "-", "Remedy Id : " + remedyId + " has multiple occurrences. Remedy id should be unique."));
        if (isBlank(remedyId)) {
            errors.add(new CsvImportError("RemedyId", "-", "Remedy Id is not present for caller : " + providerCsvRequest.getPrimaryContactNumber()));
            return;
        }
        remedyIds.add(remedyId);
    }

}

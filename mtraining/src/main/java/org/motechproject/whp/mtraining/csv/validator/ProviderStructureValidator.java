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
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.from;

@Component
public class ProviderStructureValidator {

    public List<CsvImportError> validate(List<ProviderCsvRequest> providerCsvRequests) {
        ArrayList<CsvImportError> errors = new ArrayList<>();
        Set<String> remediIds = new HashSet<>();
        Set<String> contactNumbers = new HashSet<>();
        for (ProviderCsvRequest providerCsvRequest : providerCsvRequests) {
            String remediId = providerCsvRequest.getRemedi_id();
            validateRemediId(errors, providerCsvRequest, remediIds);
            validatePrimaryContactNumber(errors, providerCsvRequest, contactNumbers);

            if (from(providerCsvRequest.getProviderstatus()) == null)
                errors.add(new CsvImportError("Provider Status", "-", "Provider Status for Remedi Id: " + remediId + " is blank or invalid."));

            if (isBlank(providerCsvRequest.getBlock()))
                errors.add(new CsvImportError("Block", "-", "Block is blank for Remedi Id: " + remediId + "."));

            if (isBlank(providerCsvRequest.getDistrict()))
                errors.add(new CsvImportError("District", "-", "District is blank for Remedi Id: " + remediId + "."));

            if (isBlank(providerCsvRequest.getState()))
                errors.add(new CsvImportError("State", "-", "State is blank for Remedi Id: " + remediId + "."));

        }
        return errors;
    }

    private void validatePrimaryContactNumber(ArrayList<CsvImportError> errors, ProviderCsvRequest providerCsvRequest, Set<String> contactNumbers) {
        String primaryContactNumber = providerCsvRequest.getPrimary_contact_number();
        if (contactNumbers.contains(primaryContactNumber))
            errors.add(new CsvImportError("Primary Contact Number", "-", "Primary Contact Number " + primaryContactNumber + " has multiple occurrences."));
        if (isBlank(primaryContactNumber) || !isNumeric(primaryContactNumber) || primaryContactNumber.length() != 10) {
            errors.add(new CsvImportError("Primary Contact Number", "-", "Primary Contact Number is invalid for Remedi Id: " + providerCsvRequest.getRemedi_id() + ". It should be a 10 digit phone number."));
            return;
        }
        contactNumbers.add(primaryContactNumber);
    }

    private void validateRemediId(ArrayList<CsvImportError> errors, ProviderCsvRequest providerCsvRequest, Set<String> remediIds) {
        String remediId = providerCsvRequest.getRemedi_id();
        if (remediIds.contains(remediId))
            errors.add(new CsvImportError("RemediId", "-", "Remedi Id : " + remediId + " has multiple occurrences. Remedi id should be unique."));
        if (isBlank(remediId)) {
            errors.add(new CsvImportError("RemediId", "-", "Remedi Id is not present for caller : " + providerCsvRequest.getPrimary_contact_number()));
            return;
        }
        remediIds.add(remediId);
    }

}

package de.lise.terradoc.core.customization.converter;

import de.lise.terradoc.core.state.TerraformResource;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ResourceTypeConverter {

    public String getReadableResourceTyp(TerraformResource resource) {
        if(resource.getType() == null || resource.getType().isEmpty()) {
            return "";
        }
        String[] parts = resource.getType().split("_");
        for(int i=0; i<parts.length; i++) {
            parts[i] = convertTypePart(parts[i]);
        }
        return String.join(" ", Arrays.stream(parts).collect(Collectors.toList()));
    }


    private String convertTypePart(String e) {
        switch (e) {
            case "aws":
                return "AWS";
        }

        return e.substring(0, 1).toUpperCase() + e.substring(1);
    }

}

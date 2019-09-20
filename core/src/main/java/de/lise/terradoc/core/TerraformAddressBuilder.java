package de.lise.terradoc.core;

import org.apache.commons.lang3.StringUtils;

public final class TerraformAddressBuilder {
    private TerraformAddressBuilder() {
    }

    public static String concat(String parent, String extension) {
        if(StringUtils.isEmpty(parent) && StringUtils.isEmpty(extension)) {
            return "";
        }else if(StringUtils.isEmpty(parent)) {
            return extension;
        } else if(StringUtils.isEmpty(extension)) {
            return parent;
        }
        return String.format("%s.%s", parent, extension);
    }

    public static String concat(String... parts) {
        String result = "";
        for(String part : parts) {
            result = concat(result, part);
        }
        return result;
    }

}

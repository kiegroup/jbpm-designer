package org.jbpm.designer.validation;

import org.uberfire.ext.editor.commons.backend.validation.FileNameValidator;
/**
 * Validator of BPMN2 process file names
 */
//@ApplicationScoped
public class BPMN2FileNameValidator /*implements FileNameValidator*/ {

//    private static List<String> EXTRA_INVALID_FILENAME_CHARS = Arrays.asList( new String[]{ "+" } );
//
//    @Inject
//    private Bpmn2TypeDefinition resourceType;
//
//    @Override
//    public int getPriority() {
//        return 1;
//    }
//
//    @Override
//    public boolean accept( final String fileName ) {
//        return fileName.endsWith( "." + resourceType.getSuffix() );
//    }
//
//    @Override
//    public boolean accept( final Path path ) {
//        return resourceType.accept( path );
//    }
//
//    @Override
//    public boolean isValid( final String value ) {
//        if ( !( processAssetFileNameValid( value ) ) ) {
//            return false;
//        }
//        return ValidationUtils.isFileName( value );
//    }
//
//    private boolean processAssetFileNameValid( String str ) {
//        for ( String item : EXTRA_INVALID_FILENAME_CHARS ) {
//            if ( str.contains( item ) ) {
//                return false;
//            }
//        }
//        return true;
//    }

}

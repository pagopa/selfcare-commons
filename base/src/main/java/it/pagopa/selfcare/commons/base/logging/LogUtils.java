package it.pagopa.selfcare.commons.base.logging;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public abstract class LogUtils {

    private LogUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static final Marker CONFIDENTIAL_MARKER = MarkerFactory.getMarker("CONFIDENTIAL");

}

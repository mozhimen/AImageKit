package com.mozhimen.imagek.matisse.widgets.longimage.commons;

/**
 * Interface for decoder (and region decoder) factories.
 *
 * @param <T> the class of decoder that will be produced.
 */
public interface IDecoderFactory<T> {
    /**
     * Produce a new instance of a decoder with type {@link T}.
     *
     * @return a new instance of your decoder.
     */
    T make() throws IllegalAccessException, InstantiationException;
}

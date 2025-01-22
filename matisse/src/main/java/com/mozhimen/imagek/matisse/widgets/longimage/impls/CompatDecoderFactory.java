package com.mozhimen.imagek.matisse.widgets.longimage.impls;


import androidx.annotation.NonNull;

import com.mozhimen.imagek.matisse.widgets.longimage.commons.IDecoderFactory;

/**
 * Compatibility factory to instantiate decoders with empty public constructors.
 *
 * @param <T> The base type of the decoder this factory will produce.
 */
public class CompatDecoderFactory<T> implements IDecoderFactory<T> {
    private Class<? extends T> clazz;

    public CompatDecoderFactory(@NonNull Class<? extends T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T make() throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
    }
}

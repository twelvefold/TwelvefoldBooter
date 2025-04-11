# TwelvefoldBooter
TwelvefoldBooter is a mixin mod utility to allow developers to load mixins both early and late, allowing for modifying  
classes that normally could not be modified otherwise.

## Current features include;
- Enqueue mixins to be applied both early and late, to allow for for modifying Vanilla/Forge classes, as well as mod classes
- Enable/disable enqueued mixins prior to application through the use of a Supplier, to allow developer control over optional mixins
- Disallow other TwelvefoldBooter enqueued mixins from applying, for testing or tweaking purposes
- Shadows Mixin 0.8.7 and MixinExtras 0.4.1

## For developer usage:

In your IFMLLoadingPlugin init, instead of `Mixins.addConfiguration("mixinConfigName")`, use `TwelvefoldRegistryAPI.enqueueEarlyMixin("mixinConfigName")`.  
If you are registering it as a late-loaded mixin use annotation `@LateMixinLoader("mixinConfigName")`.  
That is all that is required, and TwelvefoldBooter takes care of the rest.

If you have any issues/suggestions/requests, please post them to the Github issue tracker linked above.
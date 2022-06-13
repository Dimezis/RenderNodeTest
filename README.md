# RenderNodeTest
Drawing the View hierarchy on a `RenderNode` and then drawing this `RenderNode` on a system Canvas causes a crash in RenderThread.

```
signal 11 (SIGSEGV), code 1 (SEGV_MAPERR), fault addr 0x78cdb63fb0
Cause: stack pointer is close to top of stack; likely stack overflow.
    x0  b4000079fd8ef510  x1  b400007a1d8f7e40  x2  00000078cdc5b9b0  x3  0000000000000000
    x4  00000078cdb641f0  x5  00000078cdbdf5f4  x6  00040446000405ba  x7  0004028100040611
    x8  0000007bf000b7d0  x9  0000000000000000  x10 0000000000000010  x11 0000000000000000
    x12 0000000000000033  x13 0000000000000050  x14 b400007a9d9243b0  x15 0000000000000001
    x16 b400007a9d9243b0  x17 0000007be5baac78  x18 00000078cbd7c000  x19 00000078cdc5b9b0
    x20 b400007b1d8ea430  x21 0000007bf082e000  x22 00000078cdc5b7b0  x23 0000000000000000
    x24 00000078cdc5c000  x25 00000078cdc5b7b0  x26 00000078cdc5c000  x27 b400007a1d8f7d90
    x28 0000000000000001  x29 00000078cdb640f0
    lr  0000007bf00c3fe8  sp  00000078cdb64010  pc  0000007bf000b7d0  pst 0000000060001000
backtrace:
      #00 pc 000000000020b7d0  /system/lib64/libhwui.so (android::uirenderer::skiapipeline::SkiaPipeline::pinImages(std::__1::vector<SkImage*, std::__1::allocator<SkImage*> >&)+0) (BuildId: 2ce870828ed7d9ef79cd9d261babecd1)
      #01 pc 00000000002c3fe4  /system/lib64/libhwui.so (android::uirenderer::skiapipeline::SkiaDisplayList::prepareListAndChildren(android::uirenderer::TreeObserver&, android::uirenderer::TreeInfo&, bool, std::__1::function<void (android::uirenderer::RenderNode*, android::uirenderer::TreeObserver&, android::uirenderer::TreeInfo&, bool)>)+236) (BuildId: 2ce870828ed7d9ef79cd9d261babecd1)
      #02 pc 00000000002c2a94  /system/lib64/libhwui.so (android::uirenderer::RenderNode::prepareTreeImpl(android::uirenderer::TreeObserver&, android::uirenderer::TreeInfo&, bool)+1332) (BuildId: 2ce870828ed7d9ef79cd9d261babecd1)
      #03 pc 00000000002c2540  /system/lib64/libhwui.so (_ZNSt3__110__function6__funcIZN7android10uirenderer10RenderNode15prepareTreeImplERNS3_12TreeObserverERNS3_8TreeInfoEbE3$_0NS_9allocatorIS9_EEFvPS4_S6_S8_bEEclEOSC_S6_S8_Ob$907f77e9b59bd29450c46d69c21b9e58+40) (BuildId: 2ce870828ed7d9ef79cd9d261babecd1)
      #04 pc 00000000002c416c  /system/lib64/libhwui.so (android::uirenderer::skiapipeline::SkiaDisplayList::prepareListAndChildren(android::uirenderer::TreeObserver&, android::uirenderer::TreeInfo&, bool, std::__1::function<void (android::uirenderer::RenderNode*, android::uirenderer::TreeObserver&, android::uirenderer::TreeInfo&, bool)>)+628) (BuildId: 2ce870828ed7d9ef79cd9d261babecd1)
      #05 pc 00000000002c2a94  /system/lib64/libhwui.so (android::uirenderer::RenderNode::prepareTreeImpl(android::uirenderer::TreeObserver&, android::uirenderer::TreeInfo&, bool)+1332) (BuildId: 2ce870828ed7d9ef79cd9d261babecd1)
      #06 pc 00000000002c2540  /system/lib64/libhwui.so (_ZNSt3__110__function6__funcIZN7android10uirenderer10RenderNode15prepareTreeImplERNS3_12TreeObserverERNS3_8TreeInfoEbE3$_0NS_9allocatorIS9_EEFvPS4_S6_S8_bEEclEOSC_S6_S8_Ob$907f77e9b59bd29450c46d69c21b9e58+40) (BuildId: 2ce870828ed7d9ef79cd9d261babecd1)

      ... And so on and so forth
```

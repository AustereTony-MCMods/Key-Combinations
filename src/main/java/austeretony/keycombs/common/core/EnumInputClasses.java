package austeretony.keycombs.common.core;

import java.util.Iterator;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public enum EnumInputClasses {

    FML_KEY_HANDLER("FML", "KeyHandler", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),

    FORGE_GUI_CONTROLS_SCROLL_PANEL("Minecraft", "GuiControlsScrollPanel", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),

    MC_KEY_BINDING("Minecraft", "KeyBinding", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_MINECRAFT("Minecraft", "Minecraft", 0, 0),
    MC_GAME_SETTINGS("Minecraft", "GameSettings", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_GUI_SCREEN("Minecraft", "GuiScreen", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_GUI_CONTAINER("Minecraft", "GuiContainer", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

    private static final String HOOKS_CLASS = "austeretony/keycombs/common/core/KeyCombinationsHooks";

    public final String domain, clazz;

    public final int readerFlags, writerFlags;

    EnumInputClasses(String domain, String clazz, int readerFlags, int writerFlags) {
        this.domain = domain;
        this.clazz = clazz;
        this.readerFlags = readerFlags;
        this.writerFlags = writerFlags;
    }

    public boolean patch(ClassNode classNode) {
        switch (this) {
        case FML_KEY_HANDLER:
            return patchFMLKeyHandler(classNode);

        case FORGE_GUI_CONTROLS_SCROLL_PANEL:
            return patchMCGuiControlsScrollPanel(classNode);

        case MC_KEY_BINDING:
            return patchMCKeyBinding(classNode);
        case MC_MINECRAFT:
            return patchMCMinecraft(classNode);
        case MC_GAME_SETTINGS:
            return patchMCGameSettings(classNode);
        case MC_GUI_SCREEN:
            return patchMCGuiScreen(classNode);
        case MC_GUI_CONTAINER:
            return patchMCGuiContainer(classNode);

        }
        return false;
    }

    private boolean patchFMLKeyHandler(ClassNode classNode) {
        String 
        keyTickMethodName = "keyTick",
        keyBindingClassName = KeyCombinationsCorePlugin.isObfuscated() ? "ats" : "net/minecraft/client/settings/KeyBinding";
        boolean isSuccessful = false;
        int ifeqCount = 0;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(keyTickMethodName)) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.IFEQ) {
                        ifeqCount++;
                        if (ifeqCount == 3) {
                            InsnList nodesList = new InsnList();                 
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 4));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isKeyPressed", "(L" + keyBindingClassName + ";)Z"));
                            nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn).label));
                            methodNode.instructions.insertBefore(currentInsn.getPrevious(), nodesList); 
                            break;
                        }
                    }
                }
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCKeyBinding(ClassNode classNode) {
        String 
        onTickMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "onTick",
                setKeyBindStateMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "setKeyBindState",
                        stringClassName = "java/lang/String",
                        keyBindingClassName = KeyCombinationsCorePlugin.isObfuscated() ? "ats" : "net/minecraft/client/settings/KeyBinding";
        boolean isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(onTickMethodName) && methodNode.desc.equals("(I)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ILOAD) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 0));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "onTick", "(I)V"));
                        nodesList.add(new InsnNode(Opcodes.RETURN));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious(), nodesList); 
                        break;
                    }
                }
            }
            if (methodNode.name.equals(setKeyBindStateMethodName) && methodNode.desc.equals("(IZ)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ILOAD) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 0));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 1));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setKeyBindState", "(IZ)V"));
                        nodesList.add(new InsnNode(Opcodes.RETURN));
                        methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        break;
                    }
                }
            }
            if (methodNode.name.equals("<init>") && methodNode.desc.equals("(L" + stringClassName + ";I)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.RETURN) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "wrapKeyBinding", "(L" + keyBindingClassName + ";)V"));
                        methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        break;
                    }
                }    
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCMinecraft(ClassNode classNode) {
        String 
        runTickMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "k" : "runTick",
                screenshotListenerMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "V" : "screenshotListener";
        int 
        bipushCount = 0,
        iconstCount = 0;
        boolean isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(screenshotListenerMethodName) && methodNode.desc.equals("()V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.BIPUSH) {
                        methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getScreenshotKeyCode", "()I")); 
                        insnIterator.remove();
                        break;
                    }
                }              
            }
            if (methodNode.name.equals(runTickMethodName) && methodNode.desc.equals("()V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.BIPUSH) {
                        bipushCount++;
                        if (bipushCount == 7 || bipushCount == 10 || bipushCount == 12 || bipushCount == 14 || bipushCount == 18 || bipushCount == 20 || bipushCount == 22 || bipushCount == 24 || bipushCount == 26) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getDebugScreenKeyCode", "()I")); 
                            insnIterator.remove();
                        }
                        if (bipushCount == 8) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isFullscreenKeyPressed", "(I)Z")); 
                            ((JumpInsnNode) currentInsn.getNext()).setOpcode(Opcodes.IFEQ);
                            insnIterator.remove();               
                        }
                        if (bipushCount == 25) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isHideHUDKeyPressed", "(I)Z")); 
                            ((JumpInsnNode) currentInsn.getNext()).setOpcode(Opcodes.IFEQ);
                            insnIterator.remove();               
                        } 
                        if (bipushCount == 27) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isTogglePerspectiveKeyPressed", "(I)Z")); 
                            ((JumpInsnNode) currentInsn.getNext()).setOpcode(Opcodes.IFEQ);
                            insnIterator.remove();  
                        }
                        if (bipushCount == 28) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isSmoothCameraKeyPressed", "(I)Z")); 
                            ((JumpInsnNode) currentInsn.getNext()).setOpcode(Opcodes.IFEQ);
                            insnIterator.remove();  
                            break;
                        }
                    }                                                                       
                    if (currentInsn.getOpcode() == Opcodes.ICONST_1) {
                        iconstCount++;
                        if (iconstCount == 5) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isQuitKeyPressed", "(I)Z")); 
                            ((JumpInsnNode) currentInsn.getNext()).setOpcode(Opcodes.IFEQ);
                            insnIterator.remove();
                        }
                    }               
                }    
                isSuccessful = true;
                break;
            }           
        }
        return isSuccessful;
    }

    private boolean patchMCGameSettings(ClassNode classNode) {
        String 
        loadOptionsMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "loadOptions",
                saveOptionsMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "b" : "saveOptions",
                        stringClassName = "java/lang/String",
                        printWriterClassName = "java/io/PrintWriter";
        boolean isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(loadOptionsMethodName) && methodNode.desc.equals("()V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.IF_ICMPGE) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 3));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "loadControlsFromOptionsFile", "([L" + stringClassName + ";)Z"));                   
                        nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn).label));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious().getPrevious().getPrevious().getPrevious(), nodesList);
                        break;
                    }
                }   
            }
            if (methodNode.name.equals(saveOptionsMethodName) && methodNode.desc.equals("()V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.IF_ICMPGE) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "saveControlsToOptionsFile", "(L" + printWriterClassName + ";)Z"));          
                        nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn).label));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious().getPrevious().getPrevious().getPrevious(), nodesList);
                        break;
                    }
                }   
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCGuiScreen(ClassNode classNode) {
        String keyTypedMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "keyTyped";  
        boolean isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(keyTypedMethodName) && methodNode.desc.equals("(CI)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ICONST_1) {
                        methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getQuitKeyCode", "()I")); 
                        insnIterator.remove();
                        break;
                    }
                }
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCGuiControlsScrollPanel(ClassNode classNode) {
        String
        controlsFieldName = "controls",
        selectedFieldName = "selected",
        mouseXFieldName = "_mouseX",
        mouseYFieldName = "_mouseY",
        elementClickedMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "elementClicked",
                drawScreenMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "drawScreen",
                        drawSlotMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "drawSlot",
                                keyTypedMethodName = "keyTyped",
                                guiControlsClassName = KeyCombinationsCorePlugin.isObfuscated() ? "auy" : "net/minecraft/client/gui/GuiControls",
                                        tesselatorClassName = KeyCombinationsCorePlugin.isObfuscated() ? "bfq" : "net/minecraft/client/renderer/Tessellator",
                                                guiControlsScrollPanelClassName = "net/minecraftforge/client/GuiControlsScrollPanel";
        boolean isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(elementClickedMethodName) && methodNode.desc.equals("(IZ)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.BIPUSH) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsScrollPanelClassName, selectedFieldName, "I"));
                        nodesList.add(new IntInsnNode(Opcodes.BIPUSH, - 100));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setKeyModifierAndCode", "(II)V"));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious().getPrevious().getPrevious().getPrevious(), nodesList); 
                        break;
                    }
                }               
            }
            if (methodNode.name.equals(drawScreenMethodName) && methodNode.desc.equals("(IIF)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.BIPUSH) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsScrollPanelClassName, selectedFieldName, "I"));
                        nodesList.add(new IntInsnNode(Opcodes.BIPUSH, - 100));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/wjgl/input/Mouse", "getEventButton", "()I"));
                        nodesList.add(new InsnNode(Opcodes.IADD));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setKeyModifierAndCode", "(II)V"));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious().getPrevious().getPrevious().getPrevious(), nodesList); 
                        break;
                    }
                }               
            }
            if (methodNode.name.equals(drawSlotMethodName) && methodNode.desc.equals("(IIIIL" + tesselatorClassName + ";)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsScrollPanelClassName, controlsFieldName, "L" + guiControlsClassName + ";"));
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsScrollPanelClassName, selectedFieldName, "I"));
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsScrollPanelClassName, mouseXFieldName, "I"));
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsScrollPanelClassName, mouseYFieldName, "I"));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 1));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 3));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 4));
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 5));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "drawSlot", "(L" + guiControlsClassName + ";IIIIIIIL" + tesselatorClassName + ";)V"));
                        nodesList.add(new InsnNode(Opcodes.RETURN));
                        methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        break;
                    }
                }             
            }
            if (methodNode.name.equals(keyTypedMethodName) && methodNode.desc.equals("(CI)Z")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.IF_ICMPEQ) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsScrollPanelClassName, selectedFieldName, "I"));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setKeyModifierAndCode", "(II)V"));
                        methodNode.instructions.insert(currentInsn, nodesList); 
                        break;
                    }
                }
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCGuiContainer(ClassNode classNode) {
        return patchMCGuiScreen(classNode);
    }
}

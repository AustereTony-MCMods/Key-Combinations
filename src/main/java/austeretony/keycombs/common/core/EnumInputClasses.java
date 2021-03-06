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

    MC_KEY_BINDING("Minecraft", "KeyBinding", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_MINECRAFT("Minecraft", "Minecraft", 0, 0),
    MC_GAME_SETTINGS("Minecraft", "GameSettings", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_GUI_SCREEN("Minecraft", "GuiScreen", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_GUI_CONTROLS("Minecraft", "GuiControls", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_KEY_ENTRY("Minecraft", "GuiNewKeyBindingList$KeyEntry", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),
    MC_GUI_CONTAINER("Minecraft", "GuiContainer", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES),    

    MM_KEYBOARD_HANDLER("MineMenu", "KeyboardHandler", 0, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

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
        case MC_KEY_BINDING:
            return patchMCKeyBinding(classNode);
        case MC_MINECRAFT:
            return patchMCMinecraft(classNode);
        case MC_GAME_SETTINGS:
            return patchMCGameSettings(classNode);
        case MC_GUI_SCREEN:
            return patchMCGuiScreen(classNode);
        case MC_GUI_CONTROLS:
            return patchMCGuiControls(classNode);
        case MC_KEY_ENTRY:
            return patchMCKeyEntry(classNode);
        case MC_GUI_CONTAINER:
            return patchMCGuiContainer(classNode);

        case MM_KEYBOARD_HANDLER:
            return patchMMKeyboardHanndler(classNode);
        }
        return false;
    }

    private boolean patchMCKeyBinding(ClassNode classNode) {
        String 
        onTickMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "onTick",
                isKeyPressedMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "d" : "getIsKeyPressed",
                        setKeyBindStateMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "setKeyBindState",
                                stringClassName = "java/lang/String",
                                keyBindingClassName = KeyCombinationsCorePlugin.isObfuscated() ? "bal" : "net/minecraft/client/settings/KeyBinding";
        boolean isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(onTickMethodName) && methodNode.desc.equals("(I)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.IFNULL) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 0));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "lookupActive", "(I)L" + keyBindingClassName + ";", false));
                        nodesList.add(new VarInsnNode(Opcodes.ASTORE, 1));
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
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setKeybindingsState", "(IZ)V", false));
                        nodesList.add(new InsnNode(Opcodes.RETURN));
                        methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        break;
                    }
                }
            }
            if (methodNode.name.equals("<init>") && methodNode.desc.equals("(L" + stringClassName + ";IL" + stringClassName + ";)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.RETURN) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "wrapKeyBinding", "(L" + keyBindingClassName + ";)V", false));
                        methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        break;
                    }
                }                
            }
            if (methodNode.name.equals(isKeyPressedMethodName) && methodNode.desc.equals("()Z")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {    
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isKeyPressed", "(L" + keyBindingClassName + ";)Z", false));
                        nodesList.add(new InsnNode(Opcodes.IRETURN));
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
        String runTickMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "p" : "runTick";
        int 
        bipushCount = 0,
        iconstCount = 0;
        boolean isSuccessful = false;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(runTickMethodName) && methodNode.desc.equals("()V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.BIPUSH) {
                        bipushCount++;
                        if (bipushCount == 4 || bipushCount == 6 || bipushCount == 9 || bipushCount == 11 || bipushCount == 13 || bipushCount == 17 || bipushCount == 19 || bipushCount == 21 || bipushCount == 23 || bipushCount == 25) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getDebugScreenKeyCode", "()I", false)); 
                            insnIterator.remove();
                            if (bipushCount == 25)
                                break; 
                        }
                        if (bipushCount == 7) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getDisableShaderKeyCode", "()I", false)); 
                            insnIterator.remove();
                        }
                        if (bipushCount == 24) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isHideHUDKeyPressed", "(I)Z", false)); 
                            ((JumpInsnNode) currentInsn.getNext()).setOpcode(Opcodes.IFEQ);
                            insnIterator.remove();               
                        } 
                    }                                                                       
                    if (currentInsn.getOpcode() == Opcodes.ICONST_1) {
                        iconstCount++;
                        if (iconstCount == 4) {
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isQuitKeyPressed", "(I)Z", false)); 
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
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "loadControlsFromOptionsFile", "([L" + stringClassName + ";)Z", false));                    
                        nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn).label));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious().getPrevious(), nodesList);
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
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "saveControlsToOptionsFile", "(L" + printWriterClassName + ";)Z", false));          
                        nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn).label));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious().getPrevious(), nodesList);
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
                        methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getQuitKeyCode", "()I", false)); 
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

    private boolean patchMCGuiControls(ClassNode classNode) {
        String 
        buttonIdFieldName = KeyCombinationsCorePlugin.isObfuscated() ? "f" : "buttonId",
                resetButtonFieldName = KeyCombinationsCorePlugin.isObfuscated() ? "t" : "field_146493_s",
                        actionPerformedMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "actionPerformed",
                                mouseClickedMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "mouseClicked",
                                        keyTypedMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "keyTyped",
                                                drawScreenMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "drawScreen",
                                                        guiScreenClassName = KeyCombinationsCorePlugin.isObfuscated() ? "bdw" : "net/minecraft/client/gui/GuiScreen",
                                                                guiButtonClassName = KeyCombinationsCorePlugin.isObfuscated() ? "bcb" : "net/minecraft/client/gui/GuiButton",
                                                                        keyModifierClassName = "austeretony/keycombs/client/keybindings/EnumKeyModifier",
                                                                        guiControlsClassName = KeyCombinationsCorePlugin.isObfuscated() ? "bew" : "net/minecraft/client/gui/GuiControls",
                                                                                keyBindingClassName = KeyCombinationsCorePlugin.isObfuscated() ? "bal" : "net/minecraft/client/settings/KeyBinding";
        boolean isSuccessful = false;
        int aloadCount = 0;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(actionPerformedMethodName) && methodNode.desc.equals("(L" + guiButtonClassName + ";)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {
                        aloadCount++;
                        if (aloadCount == 5) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "resetAllKeys", "()V", false));
                            nodesList.add(new InsnNode(Opcodes.RETURN));
                            methodNode.instructions.insertBefore(currentInsn, nodesList); 
                            break;
                        }                       
                    }
                }               
            }
            if (methodNode.name.equals(mouseClickedMethodName) && methodNode.desc.equals("(III)V")) {
                aloadCount = 0;
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {
                        aloadCount++;
                        if (aloadCount == 2) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, buttonIdFieldName, "L" + keyBindingClassName + ";"));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, keyModifierClassName, "getActiveModifier", "()L" + keyModifierClassName + ";", false));
                            nodesList.add(new IntInsnNode(Opcodes.BIPUSH, - 100));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 3));
                            nodesList.add(new InsnNode(Opcodes.IADD));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setKeyModifierAndCode", "(L" + keyBindingClassName + ";L" + keyModifierClassName + ";I)V", false));
                            methodNode.instructions.insertBefore(currentInsn, nodesList); 
                            break;
                        }                       
                    }
                }               
            }
            if (methodNode.name.equals(keyTypedMethodName) && methodNode.desc.equals("(CI)V")) {
                aloadCount = 0;
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {
                        aloadCount++;
                        if (aloadCount == 2) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, buttonIdFieldName, "L" + keyBindingClassName + ";"));
                            nodesList.add(new FieldInsnNode(Opcodes.GETSTATIC, keyModifierClassName, "NONE", "L" + keyModifierClassName + ";"));
                            nodesList.add(new IntInsnNode(Opcodes.BIPUSH, 0));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setKeyModifierAndCode", "(L" + keyBindingClassName + ";L" + keyModifierClassName + ";I)V", false));
                            methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        }  
                        if (aloadCount == 4) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, buttonIdFieldName, "L" + keyBindingClassName + ";"));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, keyModifierClassName, "getActiveModifier", "()L" + keyModifierClassName + ";", false));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setKeyModifierAndCode", "(L" + keyBindingClassName + ";L" + keyModifierClassName + ";I)V", false));
                            methodNode.instructions.insertBefore(currentInsn, nodesList); 

                            break;
                        }   
                    }
                    if (currentInsn.getOpcode() == Opcodes.ACONST_NULL) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, buttonIdFieldName, "L" + keyBindingClassName + ";"));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "resetKeyBinding", "(L" + keyBindingClassName + ";I)L" + keyBindingClassName + ";", false));
                        methodNode.instructions.insertBefore(currentInsn, nodesList); 
                        insnIterator.remove();
                        break;
                    }
                }
            }
            if (methodNode.name.equals(drawScreenMethodName) && methodNode.desc.equals("(IIF)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ISTORE) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, resetButtonFieldName, "L" + guiButtonClassName + ";"));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setResetButtonState", "(L" + guiButtonClassName + ";)V", false));
                        nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 1));
                        nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                        nodesList.add(new VarInsnNode(Opcodes.FLOAD, 3));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, guiScreenClassName, drawScreenMethodName, "(IIF)V", false));
                        nodesList.add(new InsnNode(Opcodes.RETURN));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious(), nodesList); 
                        isSuccessful = true;
                        break;
                    }
                }  
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCKeyEntry(ClassNode classNode) {
        String
        changeButtonFieldName = KeyCombinationsCorePlugin.isObfuscated() ? "d" : "btnChangeKeyBinding",
                resetButtonFieldName = KeyCombinationsCorePlugin.isObfuscated() ? "e" : "btnReset",
                        keyBindingFieldName = KeyCombinationsCorePlugin.isObfuscated() ? "b" : "field_148282_b",
                                drawEntryMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "drawEntry",
                                        mousePressedMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "mousePressed",
                                                keyEntryClassName = KeyCombinationsCorePlugin.isObfuscated() ? "bev" : "net/minecraft/client/gui/GuiKeyBindingList$KeyEntry",
                                                        tesselatorClassName = KeyCombinationsCorePlugin.isObfuscated() ? "bmh" : "net/minecraft/client/renderer/Tessellator",
                                                                guiButtonClassName = KeyCombinationsCorePlugin.isObfuscated() ? "bcb" : "net/minecraft/client/gui/GuiButton",
                                                                        keyBindingClassName = KeyCombinationsCorePlugin.isObfuscated() ? "bal" : "net/minecraft/client/settings/KeyBinding";
        boolean isSuccessful = false;
        int
        ifeqCount = 0,
        aloadCount = 0;
        AbstractInsnNode currentInsn;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(drawEntryMethodName) && methodNode.desc.equals("(IIIIIL" + tesselatorClassName + ";IIZ)V")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {    
                        aloadCount++;
                        if (aloadCount == 7) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, keyEntryClassName, changeButtonFieldName, "L" + guiButtonClassName + ";"));
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, keyEntryClassName, resetButtonFieldName, "L" + guiButtonClassName + ";"));
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, keyEntryClassName, keyBindingFieldName, "L" + keyBindingClassName + ";"));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 10));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 3));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 7));
                            nodesList.add(new VarInsnNode(Opcodes.ILOAD, 8));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "drawCuiControlsKeyEntry", "(L" + guiButtonClassName + ";L" + guiButtonClassName + ";L" + keyBindingClassName + ";ZIIII)V", false));
                            nodesList.add(new InsnNode(Opcodes.RETURN));
                            methodNode.instructions.insertBefore(currentInsn, nodesList); 
                            break;
                        }
                    }
                }
            }
            if (methodNode.name.equals(mousePressedMethodName) && methodNode.desc.equals("(IIIIII)Z")) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next(); 
                    if (currentInsn.getOpcode() == Opcodes.IFEQ) {    
                        ifeqCount++;
                        if (ifeqCount == 2) {
                            InsnList nodesList = new InsnList();
                            nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, keyEntryClassName, keyBindingFieldName, "L" + keyBindingClassName + ";"));
                            nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setToDefault", "(L" + keyBindingClassName + ";)V", false));
                            methodNode.instructions.insertBefore(currentInsn.getNext(), nodesList); 
                            isSuccessful = true;
                            break;
                        }
                    }
                }
                break;
            }
        }
        return isSuccessful;
    }

    private boolean patchMCGuiContainer(ClassNode classNode) {
        return patchMCGuiScreen(classNode);
    }

    private boolean patchMMKeyboardHanndler(ClassNode classNode) {
        String
        wheelFieldName = "WHEEL",
        onClientTickMethodName = "onClientTick",
        keyboardHandlerClassname = "dmillerw/menu/handler/KeyboardHandler",
        keyBindingClassName = "net/minecraft/client/settings/KeyBinding";
        boolean isSuccessful = false;
        AbstractInsnNode currentInsn;    

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(onClientTickMethodName)) {
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
                while (insnIterator.hasNext()) {
                    currentInsn = insnIterator.next();
                    if (currentInsn.getOpcode() == Opcodes.IF_ICMPEQ) {
                        InsnList nodesList = new InsnList();
                        nodesList.add(new FieldInsnNode(Opcodes.GETSTATIC, keyboardHandlerClassname, wheelFieldName, "L" + keyBindingClassName + ";"));
                        nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isMineMenuKeyPressed", "(L" + keyBindingClassName + ";)Z", false));
                        nodesList.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) currentInsn).label));
                        methodNode.instructions.insertBefore(currentInsn.getPrevious().getPrevious().getPrevious(), nodesList);
                        methodNode.instructions.remove(currentInsn.getPrevious().getPrevious().getPrevious());
                        methodNode.instructions.remove(currentInsn.getPrevious().getPrevious());
                        methodNode.instructions.remove(currentInsn.getPrevious());
                        methodNode.instructions.remove(currentInsn);
                        isSuccessful = true;
                        break;
                    }
                }
                break;
            }
        }
        return isSuccessful;
    }
}

package ru.austeretony.keycombs.coremod;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
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

import net.minecraft.launchwrapper.IClassTransformer;

public class KeyCombinationsClassTransformer implements IClassTransformer {

	public static final Logger CORE_LOGGER = LogManager.getLogger("Key Combinations Core");
	
	private static final String HOOKS_CLASS = "ru/austeretony/keycombs/coremod/KeyCombinationsHooks";
	 
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {    	
    	
    	switch (transformedName) {
    	
			case "net.minecraft.client.settings.GameSettings":							
				return patchGameSettings(basicClass);
			case "net.minecraft.client.settings.KeyBinding":		
				return patchKeyBinding(basicClass);				
			case "net.minecraft.client.gui.GuiKeyBindingList$KeyEntry":								
				return patchKeyEntry(basicClass);					
			case "net.minecraft.client.gui.GuiControls":		
				return patchGuiControls(basicClass);							
			case "net.minecraft.client.Minecraft":							
	    		return patchMinecraft(basicClass);			
			case "net.minecraft.client.gui.GuiScreen":							
	    		return patchGui(basicClass, true);			
			case "net.minecraft.client.gui.inventory.GuiContainer":							
	    		return patchGui(basicClass, false);	
    	}
    	
		return basicClass;
    }
    
	private byte[] patchGameSettings(byte[] basicClass) {
		
	    ClassNode classNode = new ClassNode();
	    ClassReader classReader = new ClassReader(basicClass);
	    classReader.accept(classNode, 0);
	    
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

	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.info("<GameSettings.class> patched!");   
	    	    
	    return writer.toByteArray();	
	}
	
	private byte[] patchKeyBinding(byte[] basicClass) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String 
	 	onTickMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "onTick",
	 	setKeyBindStateMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "setKeyBindState",
	    isKeyDownMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "d" : "isKeyDown",
	 	stringClassName = "java/lang/String",
	 	keyBindingClassName = KeyCombinationsCorePlugin.isObfuscated() ? "avb" : "net/minecraft/client/settings/KeyBinding";

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
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "onTick", "(I)V", false));
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
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "setKeyBindState", "(IZ)V", false));
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
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "createPropertry", "(L" + keyBindingClassName + ";)V", false));
                    	
                    	methodNode.instructions.insertBefore(currentInsn, nodesList); 
                    	
                    	break;
                    }
                }                
			}
			
			if (methodNode.name.equals(isKeyDownMethodName) && methodNode.desc.equals("()Z")) {
                
                Iterator<AbstractInsnNode> insnIterator = methodNode.instructions.iterator();
               
                while (insnIterator.hasNext()) {
                	
                    currentInsn = insnIterator.next(); 
                    
                    if (currentInsn.getOpcode() == Opcodes.ALOAD) {                   	
                    	
                    	InsnList nodesList = new InsnList();
                    	
                    	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "isKeyDown", "(L" + keyBindingClassName + ";)Z", false));
                    	nodesList.add(new InsnNode(Opcodes.IRETURN));         
                    	
                    	methodNode.instructions.insertBefore(currentInsn, nodesList); 
                    
                    	break;
                    }
                }
                
                isSuccessful = true;
                
				break;
			}
		}
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.info("<KeyBinding.class> patched!");   
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchKeyEntry(byte[] basicClass) {
				        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String
	 	changeButtonFieldName = KeyCombinationsCorePlugin.isObfuscated() ? "d" : "btnChangeKeyBinding",
	 	resetButtonFieldName = KeyCombinationsCorePlugin.isObfuscated() ? "e" : "btnReset",
	 	keyBindingFieldName = KeyCombinationsCorePlugin.isObfuscated() ? "b" : "keybinding",
	 	drawEntryMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "drawEntry",
	    mousePressedMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "mousePressed",
	 	keyEntryClassName = KeyCombinationsCorePlugin.isObfuscated() ? "ayi$b" : "net/minecraft/client/gui/GuiKeyBindingList$KeyEntry",
	    guiButtonClassName = KeyCombinationsCorePlugin.isObfuscated() ? "avs" : "net/minecraft/client/gui/GuiButton",
	 	keyBindingClassName = KeyCombinationsCorePlugin.isObfuscated() ? "avb" : "net/minecraft/client/settings/KeyBinding";
	 	
        boolean isSuccessful = false;
        
        int
        ifeqCount = 0,
        aloadCount = 0;
        
        AbstractInsnNode currentInsn;
                        
		for (MethodNode methodNode : classNode.methods) {
			
			if (methodNode.name.equals(drawEntryMethodName) && methodNode.desc.equals("(IIIIIIIZ)V")) {
																                
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
                        	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 9));
                        	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 2));
                        	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 3));
                        	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 6));
                        	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 7));
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
                    	}
                    }
                }
                
                isSuccessful = true;
                
				break;
			}
		}
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.info("<GuiKeyBindingList.KeyEntry.class> patched!");  
	            
        return writer.toByteArray();				
	}
    
	private byte[] patchGuiControls(byte[] basicClass) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String 
	 	buttonIdFieldName = KeyCombinationsCorePlugin.isObfuscated() ? "f" : "buttonId",
	 	resetButtonFieldName = KeyCombinationsCorePlugin.isObfuscated() ? "t" : "buttonReset",
	 	actionPerformedMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "actionPerformed",
	 	mouseClickedMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "mouseClicked",
	 	keyTypedMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "keyTyped",
	 	drawScreenMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "a" : "drawScreen",
	 	guiScreenClassName = KeyCombinationsCorePlugin.isObfuscated() ? "axu" : "net/minecraft/client/gui/GuiScreen",
	 	guiButtonClassName = KeyCombinationsCorePlugin.isObfuscated() ? "avs" : "net/minecraft/client/gui/GuiButton",
	 	keyModifierClassName = "ru/austeretony/keycombs/main/EnumKeyModifier",
	 	guiControlsClassName = KeyCombinationsCorePlugin.isObfuscated() ? "ayj" : "net/minecraft/client/gui/GuiControls",
	 	keyBindingClassName = KeyCombinationsCorePlugin.isObfuscated() ? "avb" : "net/minecraft/client/settings/KeyBinding";
	 	
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
                    	}   
                    	
                    	if (aloadCount == 6) {
                    		
                        	InsnList nodesList = new InsnList();
                        	
                        	nodesList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        	nodesList.add(new FieldInsnNode(Opcodes.GETFIELD, guiControlsClassName, buttonIdFieldName, "L" + keyBindingClassName + ";"));
                        	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, keyModifierClassName, "getActiveModifier", "()L" + keyModifierClassName + ";", false));
                        	nodesList.add(new VarInsnNode(Opcodes.ILOAD, 1));                       	
                        	nodesList.add(new IntInsnNode(Opcodes.BIPUSH, 256));
                        	nodesList.add(new InsnNode(Opcodes.IADD));                       	
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
                    		
                        break;
                    }
                }  
                
                isSuccessful = true;
				
				break;
			}
		}
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.info("<GuiControls.class> patched!");   
        
        return writer.toByteArray();				
	}
	
	private byte[] patchMinecraft(byte[] basicClass) {
		        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
	 	String runTickMethodName = KeyCombinationsCorePlugin.isObfuscated() ? "s" : "runTick";
	 	
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
                    	
                    	if (bipushCount == 4 || bipushCount == 6 || bipushCount == 9 || bipushCount == 11 || bipushCount == 13 || bipushCount == 15 || bipushCount == 17 || bipushCount == 19 || bipushCount == 21 || bipushCount == 23 || bipushCount == 25 || bipushCount == 27 || bipushCount == 29 || bipushCount == 31 || bipushCount == 33 || bipushCount == 35) {
                    		
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getDebugScreenKeyCode", "()I", false)); 
                    		
                    		insnIterator.remove();
                    		
                    		if (bipushCount == 35) {
                    			
                    			break; 
                    		}
                    	}
                    	
                    	if (bipushCount == 14) {
                    		
                            methodNode.instructions.insertBefore(currentInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getDisableShaderKeyCode", "()I", false)); 
                    		
                    		insnIterator.remove();
                    	}
                    	                   	
                    	if (bipushCount == 34) {
                        	                                                   	
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
		
	    ClassWriter writer = new ClassWriter(0);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.info("<Minecraft.class> patched!");
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchGui(byte[] basicClass, boolean flag) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
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
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful) {
	    	
	    	if (flag)
	    		CORE_LOGGER.info("<GuiScreen.class> patched!");   
	    	else
	    		CORE_LOGGER.info("<GuiContainer.class> patched!");   
	    }
        
        return writer.toByteArray();				
	}
}

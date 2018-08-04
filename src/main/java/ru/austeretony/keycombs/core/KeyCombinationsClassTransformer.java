package ru.austeretony.keycombs.core;

import java.io.PrintStream;
import java.util.Iterator;

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

	public static final PrintStream CORE_LOGGER = System.out;
	
	private static final String HOOKS_CLASS = "ru/austeretony/keycombs/core/KeyCombinationsHooks";
	
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {    	
    	
    	switch (transformedName) {
    	
			case "net.minecraft.client.settings.GameSettings":							
				return patchGameSettings(basicClass);
			case "net.minecraft.client.settings.KeyBinding":		
				return patchKeyBinding(basicClass);		
			case "net.minecraftforge.client.GuiControlsScrollPanel":		
				return patchGuiControlsScrollPanel(basicClass);			
			case "cpw.mods.fml.client.registry.KeyBindingRegistry$KeyHandler":		
				return patchKeyHandler(basicClass);		
			case "net.minecraft.client.Minecraft":							
	    		return patchMinecraft(basicClass);			
			case "net.minecraft.client.gui.GuiScreen":							
	    		return patchGuiScreen(basicClass, true);			
			case "net.minecraft.client.gui.inventory.GuiContainer":							
	    		return patchGuiScreen(basicClass, false);
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

	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.println("[Key Combinations Core] <GameSettings.class> patched!");   
	    	    
	    return writer.toByteArray();	
	}
	
	private byte[] patchKeyBinding(byte[] basicClass) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
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
                    	nodesList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "createPropertry", "(L" + keyBindingClassName + ";)V"));
                    	
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
	    	CORE_LOGGER.println("[Key Combinations Core] <KeyBinding.class> patched!");   
	            
        return writer.toByteArray();				
	}
		
	private byte[] patchGuiControlsScrollPanel(byte[] basicClass) {				        
				
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
                
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
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)   	
	    	CORE_LOGGER.println("[Key Combinations Core] <GuiControlsScrollPanel.class> patched!");   
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchKeyHandler(byte[] basicClass) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
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
		
	    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.println("[Key Combinations Core] <KeyBindingRegistry.KeyHandler.class> patched!");
        
        return writer.toByteArray();				
	}
	
	private byte[] patchMinecraft(byte[] basicClass) {
        
	    ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
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
		
	    ClassWriter writer = new ClassWriter(0);	    
	    classNode.accept(writer);
	    
	    if (isSuccessful)
	    	CORE_LOGGER.println("[Key Combinations Core] <Minecraft.class> patched!");
	            
        return writer.toByteArray();				
	}
	
	private byte[] patchGuiScreen(byte[] basicClass, boolean flag) {
        
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
                    		
                        methodNode.instructions.insert(currentInsn.getPrevious(), new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS_CLASS, "getQuitKeyCode", "()I")); 
                    		
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
	    		CORE_LOGGER.println("[Key Combinations Core] <GuiScreen.class> patched!");
	    	else
	    		CORE_LOGGER.println("[Key Combinations Core] <GuiContainer.class> patched!");
	    }
        
        return writer.toByteArray();				
	}
}

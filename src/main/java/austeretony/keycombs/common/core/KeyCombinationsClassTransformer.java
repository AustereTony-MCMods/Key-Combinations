package austeretony.keycombs.common.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class KeyCombinationsClassTransformer implements IClassTransformer {

    public static final Logger CORE_LOGGER = LogManager.getLogger("Key Combinations Core");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
        case "net.minecraft.client.settings.KeyBinding":
            return patch(basicClass, EnumInputClasses.MC_KEY_BINDING);
        case "net.minecraft.client.Minecraft":
            return patch(basicClass, EnumInputClasses.MC_MINECRAFT);
        case "net.minecraft.client.settings.GameSettings":
            return patch(basicClass, EnumInputClasses.MC_GAME_SETTINGS);
        case "net.minecraft.client.gui.GuiScreen":
            return patch(basicClass, EnumInputClasses.MC_GUI_SCREEN);
        case "net.minecraft.client.gui.GuiControls":
            return patch(basicClass, EnumInputClasses.MC_GUI_CONTROLS);
        case "net.minecraft.client.gui.GuiKeyBindingList$KeyEntry":
            return patch(basicClass, EnumInputClasses.MC_KEY_ENTRY);
        case "net.minecraft.client.gui.inventory.GuiContainer":
            return patch(basicClass, EnumInputClasses.MC_GUI_CONTAINER);

        case "dmillerw.menu.handler.KeyboardHandler":
            return patch(basicClass, EnumInputClasses.MM_KEYBOARD_HANDLER);
        }
        return basicClass;
    }

    private byte[] patch(byte[] basicClass, EnumInputClasses enumInput) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, enumInput.readerFlags);
        if (enumInput.patch(classNode))
            CORE_LOGGER.info(enumInput.domain + " <" + enumInput.clazz + ".class> patched!");
        ClassWriter writer = new ClassWriter(enumInput.writerFlags);        
        classNode.accept(writer);
        return writer.toByteArray();    
    }
}

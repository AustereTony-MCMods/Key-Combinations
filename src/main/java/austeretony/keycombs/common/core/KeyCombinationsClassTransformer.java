package austeretony.keycombs.common.core;

import java.io.PrintStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class KeyCombinationsClassTransformer implements IClassTransformer {

    public static final PrintStream CORE_LOGGER = System.out;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
        case "cpw.mods.fml.client.registry.KeyBindingRegistry$KeyHandler":
            return patch(basicClass, EnumInputClasses.FML_KEY_HANDLER);

        case "net.minecraftforge.client.GuiControlsScrollPanel":
            return patch(basicClass, EnumInputClasses.FORGE_GUI_CONTROLS_SCROLL_PANEL);

        case "net.minecraft.client.settings.KeyBinding":
            return patch(basicClass, EnumInputClasses.MC_KEY_BINDING);
        case "net.minecraft.client.Minecraft":
            return patch(basicClass, EnumInputClasses.MC_MINECRAFT);
        case "net.minecraft.client.settings.GameSettings":
            return patch(basicClass, EnumInputClasses.MC_GAME_SETTINGS);
        case "net.minecraft.client.gui.GuiScreen":
            return patch(basicClass, EnumInputClasses.MC_GUI_SCREEN);     
        case "net.minecraft.client.gui.inventory.GuiContainer":
            return patch(basicClass, EnumInputClasses.MC_GUI_CONTAINER);
        }
        return basicClass;
    }

    private byte[] patch(byte[] basicClass, EnumInputClasses enumInput) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, enumInput.readerFlags);
        if (enumInput.patch(classNode))
            CORE_LOGGER.println("[Key Combinations Core][INFO] " + enumInput.domain + " <" + enumInput.clazz + ".class> patched!");
        ClassWriter writer = new ClassWriter(enumInput.writerFlags);        
        classNode.accept(writer);
        return writer.toByteArray();    
    }
}

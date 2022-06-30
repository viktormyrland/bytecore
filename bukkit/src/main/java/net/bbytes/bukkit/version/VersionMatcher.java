package net.bbytes.bukkit.version;

import net.bbytes.core.VersionWrapper;
import org.bukkit.Bukkit;

public class VersionMatcher {

    public VersionWrapper match(String serverVersion) {

        try {
            return (VersionWrapper) Class.forName(getClass().getPackage().getName() + ".Wrapper" + serverVersion).newInstance();
        } catch (IllegalAccessException | InstantiationException exception) {
            throw new IllegalStateException("Failed to instantiate version wrapper for version " + serverVersion, exception);
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("ByteCore-Bukkit does not support server version \"" + serverVersion + "\"", exception);
        }
    }


}

package net.bbytes.bukkit.command;

import net.bbytes.bukkit.Main;
import net.bbytes.bukkit.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ExportYml implements CommandExecutor,TabCompleter, Listener {


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Map<String, String> map = new LinkedHashMap<>();

		for(Message message : Message.values()){
			System.out.println(message.name());
			map.put(Message.enumToYml(message.name()), message.getRaw().replaceAll("§", "&"));
		}

		DumperOptions options = new DumperOptions();
		options.setIndent(2);
		options.setPrettyFlow(true);
		options.setSplitLines(false);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setAllowUnicode(true);

		Yaml yaml = new Yaml(options);

		try {
			StringWriter stringWriter = new StringWriter();
			yaml.dump(map, stringWriter);
			OutputStreamWriter stream = new OutputStreamWriter(new FileOutputStream(Main.getInstance().getDataFolder() + "/en-us.yml"), StandardCharsets.UTF_8);
			stream.write(stringWriter.toString());
			stream.flush();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		
		return new ArrayList<String>();
	}

	
}

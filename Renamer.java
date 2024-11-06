package Tests;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;

/*
 * For each file in each folder prompt The use if they want to add the file 
 * to the destination directory and rename at the same time.
 */

public class Renamer {

	public static void main(String[] args) throws Exception {
		String tmpsrc = "/home/zirsky/Desktop/Texts";
		String tmpdst = "/home/zirsky/Temporary/Texts";
		SourceDest srcDst = new SourceDest(tmpsrc,tmpdst);
		promptCopy(srcDst);
	}
	
	static class SourceDest {
		File source;
		File dest;
		public SourceDest(String source, String dest) {
			this.source = new File(source).getAbsoluteFile();
			this.dest = new File(dest).getAbsoluteFile();
		}
	}
	public static void promptCopy(SourceDest srcDst) throws Exception{
		//Call recursive process
		promptCopy(srcDst.source, srcDst);
	}
	private static void promptCopy(File source, SourceDest srcDst) throws Exception{
		Scanner sc = new Scanner(System.in);
		String input = null;
		String name = null;
		for (File file: getDirFiles(source)) {
			System.out.println( movePrompt(file) );
			System.out.print("Enter [Y/n]: ");
			input = sc.nextLine();
			if (input.charAt(0) == 'Y' || input.charAt(0) == 'y') {
				//If destination parent folder does not exist
				if (!getNewPath(file.getParentFile(),srcDst.source,srcDst.dest).toFile().exists())
					Files.createDirectories(getNewPath(file.getParentFile(),srcDst.source,srcDst.dest));
				System.out.println(renamePrompt(file));
				System.out.print("Enter [Y/n]: ");
				input = sc.nextLine();
				if (input.charAt(0) == 'Y' || input.charAt(0) == 'y') {
					System.out.println("What would you like to rename it to :\n  > ");
					name = sc.nextLine();
					Files.copy(file.toPath(), getNewPath(file,srcDst.source,srcDst.dest,name));
				} else { name = file.getName();}
				Files.copy(file.toPath(), getNewPath(file,srcDst.source,srcDst.dest));
			}
			System.out.println("\n\n");
		} 
		for (File file: getDirDirs(source)) {
			//Call recursive process
			promptCopy(file ,srcDst);
		}
	}
	
	public static List<File> getDirFiles(File dir){
		return Arrays.asList(dir.listFiles()).stream()
				.filter((file)->(file.isFile() && isPDF(file)))
				.toList();
	}
	public static List<File> getDirDirs(File dir){
		return Arrays.asList(dir.listFiles()).stream()
				.filter((file)->(file.isDirectory()))
				.toList();
	}
	public static String movePrompt(File file) {
		String result = String.format("Would you like to move file\n%s\n",file.toString());
		return result;
	}
	public static String renamePrompt(File file) {
		String result = String.format("Would you like to rename file :\n%s\n\n",file.getName());
		return result;
	}
	public static Path getNewPath(File src, File root,File newRoot, String newName) {
		return Path.of(newRoot.toString().concat(src.getParent().toString().substring(root.toString().length())+ '/' + newName));
	}
	public static Path getNewPath(File src, File root, File newRoot) {
		return Path.of(newRoot.toString().concat(src.toString().substring(root.toString().length())));
	}
	private static boolean isPDF(File file) {
		String match = ".pdf";
		String fileName = file.toString();
		int index = fileName.lastIndexOf('.');
		if (index == -1)
			return false;
		return (match.equalsIgnoreCase(fileName.substring(index)));
	}
}

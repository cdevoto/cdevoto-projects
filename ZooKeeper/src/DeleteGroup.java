import java.util.List;

import org.apache.zookeeper.KeeperException;


public class DeleteGroup extends ConnectionWatcher {
	
	
	public void delete (String groupName) throws KeeperException, InterruptedException {
		String path = "/" + groupName;
		try {
			List<String> children = zk.getChildren(path, false);
			for (String child : children) {
				zk.delete(path + "/" + child, -1);
			}
			zk.delete(path, -1);
		} catch (KeeperException.NoNodeException e) {
			System.out.printf("Group % does not exist\n", groupName);
			System.exit(1);
		}
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("java DeleteGroup <hosts> <group>");
			System.exit(-1);
		}
		DeleteGroup deleteGroup = new DeleteGroup();
		deleteGroup.connect(args[0]);
		deleteGroup.delete(args[1]);
		deleteGroup.close();
	}

}

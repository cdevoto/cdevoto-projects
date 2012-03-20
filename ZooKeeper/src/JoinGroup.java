import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;


public class JoinGroup extends ConnectionWatcher {
	
	public void join (String groupName, String memberName) throws KeeperException, InterruptedException {
		String path = "/" + groupName + "/" + memberName;
		String createdPath = zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		System.out.println("Created " + createdPath);
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("java JoinGroup <hosts> <group> <memberName>");
			System.exit(-1);
		}
		JoinGroup joinGroup = new JoinGroup();
		joinGroup.connect(args[0]);
		joinGroup.join(args[1], args[2]);
		
		// stay alive until process is killed or thread is interrupted
		Thread.sleep(Long.MAX_VALUE);
	}

}

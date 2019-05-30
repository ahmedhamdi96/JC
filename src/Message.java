public class Message  {
	String source;
	String destination;
	String content;
	int ttl;
	
	public Message(String source,String destination,int ttl,String content) {
		this.source=source;
		this.content=content;				
		this.ttl=ttl;
		this.destination=destination;

	}
}

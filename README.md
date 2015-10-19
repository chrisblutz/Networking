Travis CI Build Status: [![Build Status](https://travis-ci.org/LutzBlox/Networking.svg?branch=master)](https://travis-ci.org/LutzBlox/Networking)
<h1>LutzBlox's Networking Library</h1>
<p>A networking library built in Java that allows developers to send data easily across networks using packets of data.</p>
<h3>Usage</h3>
<p>Setting up a basic client/server connection is as easy as writing a few simple lines of code.</p>
<h5>Server</h5>
<p>To set up the server side of a connection, create a new Server object and give it a port and a name:</p>
<pre>Server server = new Server(port, name);</pre>
<p>The Server constructor can also take several more arguments, such as maximum connections possible and a loop delay (in milliseconds) for checking for failed client connections (to remove them from the Server object):</p>
<pre>
Server server = new Server(port, name, maxConnections);
Server server = new Server(port, name, failCheck);
Server server = new Server(port, name, maxConnections, failCheck);
</pre>
<p>Servers can also have ServerListeners added to them, which allow programmers to act when certain events happen, such as a client connecting, receiving a packet from a client, a client timing out, or a client failing.  To add a ServerListener to your Server, use:</p>
<pre>
server.addNetworkListener(new ServerListener(){

  @Override
  public void onReceive(Connection connection, Packet packet){
  }
  
  @Override
  public Packet onConnect(Connection connection, Packet packet){
  
    return packet;
  }
  
  @Override
  public void onTimeout(Connection connection){
  }
  
  @Override
  public void onClientFailure(Connection connection{
  }
});
</pre>
<p>When the Server connects to a client, the <code>onConnect()</code> method is called.  The Packet passed to this method is the Packet that will be sent to the Client and its <code>onConnect()</code> method.  When a Connection receives a Packet, it is passed to the <code>onReceive()</code> method along with the Connection that received it.  When a <code>read()</code> call to one of the Server's underlying Sockets times out, the <code>onTimeout()</code> method is called.  When the Server detects that a client connected to the Server is no longer open or connected, the <code>onClientFailure()</code> method is called.</p>
<p>To start a Server, call <code>start()</code> on the Server object.  This will start the threads that accept incoming Sockets and check for failed clients.</p>
<h5>Client</h5>
<p>To set up the client side of a connection, create a new Client object and give it an IP and a port to connect to:</p>
<pre>Client client = new Client(ip, port);</pre>
<p>Similar to Servers, Clients can have ClientListeners, which allow programmers to act when the Client receives a Packet, when the Client connects to a Server, and when a Connection times out when reading.</p>
<pre>
client.addNetworkListener(new ClientListener(){

  @Override
  public void onReceive(Connection connection, Packet packet){
  }
  
  @Override
  public void onConnect(Packet packet){
  }
  
  @Override
  public void onTimeout(Connection connection){
  }
});
</pre>
<p>When the Client connects to a Server, the <code>onConnect()</code> method is called.  The Packet it receives as an argument is the one returned by the Server when calling its <code>onConnect()</code> listeners.  When the Client receives a Packet from a Server, its <code>onReceive()</code> method is called.  When the Client's Connection times out on a <code>read()</code> call, its <code>onTimeout()</code> is called.</p>
<p>To connect a Client to a Server, call its <code>connect()</code> method.</p>
<h5>Error Reporters</h5>
<p>Clients and Servers can also have ErrorReporters added to them.  ErrorReporters allow programmers to log errors in a variety of ways.</p>
<p>To add an ErrorReporter to a Client or a Server, simply call their <code>addErrorReporter()</code> method.</p>
<p>To print errors to the default System.err print stream, simply use <code>ErrorReporterFactory.newInstance()</code> with no parameters.  You can also create an ErrorReporter that writes to a stream, print stream, or file, simply by passing one of those to the <code>ErrorReporterFactory.newInstance()</code></p>
<p>(i.e. <code>ErrorReporterFactory.newInstance(stream);</code>, <code>ErrorReporterFactory.newInstance(printStream);</code>, or <code>ErrorReporterFactory.newInstance(file);</code>)</p>

const net = require('net');

function sendRawCommand(cmdLine, timeout = 2000) {
  // Promise object is used to handle asynchronous operations
  return new Promise((resolve, reject) => {
    const host = process.env.URL_CHECK_HOST || 'localhost';
    const port = Number.parseInt(process.env.URL_CHECK_PORT, 10);
    //create a new TCP socket
    const client = new net.Socket();
    //create an empty buffer to store the response
    let buffer = '';
    let done = false;
    client.setEncoding('utf8');
    client.setTimeout(timeout);

    // When the client connects, we send the command line
    client.once('connect', () => {
      client.write(cmdLine + '\n');
    });

    // When the client receives data, we append it to the buffer
    client.on('data', data => {
      if (done) return;
      buffer += data;
      done = true;
      client.destroy();
      // Resolve is a method that is called when the promise is fulfilled
      resolve(buffer.trim());
    });

    client.once('error', err => {
      if (done) return;
      done = true;
      client.destroy();
      // Reject is a method that is called when the promise is rejected
      reject(err);
    });

    client.once('timeout', () => {
      if (done) return;
      done = true;
      client.destroy();
      reject(new Error('TCP timeout'));
    });
    client.connect(port, host);
  });
}
module.exports = {
  sendRawCommand
};

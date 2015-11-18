using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;

namespace Server
{
    class Program
    {
        public static string data = null;
        const int port = 8080;


        static TcpListener startServer()
        {
            IPAddress ipAD = IPAddress.Parse("127.0.0.1");

            TcpListener listener = new TcpListener(ipAD, port);

            listener.Start();


            Console.WriteLine("Server is listening on port {0}", port);
            Console.WriteLine("Local Endpoint: {0}", listener.LocalEndpoint);
            Console.WriteLine("Waiting for connection....");

            return listener;
        }

        static Socket handleRequest(TcpListener listener)
        {
            while (true)
            {
                int bytesRead;
                byte[] buffer = new byte[1024];
                string test = null;

                Socket s = listener.AcceptSocket();
                Console.WriteLine("Connection accepted from " + s.RemoteEndPoint);

                bytesRead = s.Receive(buffer);

                char cc = ' ';
                  
                Console.WriteLine("Recieved...");
                for (int i = 0; i < bytesRead - 1; i++)
                {
                    //Console.Write(Convert.ToChar(buffer[i]));
                    cc = Convert.ToChar(buffer[i]);
                    test += cc.ToString();
                }

                Console.WriteLine(test);
                string message = encrypt();
               

                ASCIIEncoding asen = new ASCIIEncoding();
                s.Send(asen.GetBytes(message));
                s.Close();
                Console.WriteLine("sent bytes back");
            }
        }

        static string encrypt()
        {
            string line;
            Console.WriteLine("Enter which file/folder you would like to encrypt...");
            Console.WriteLine("Press CTRL+Z to exit");

            line = Console.ReadLine();
            if (line != null)
                Console.WriteLine("    " + line);

            return line;
        }

        static void Main(string[] args)
        {            
            try
            {
                TcpListener listener = startServer();
                handleRequest(listener);

                Console.ReadLine();
            } catch (Exception e) {
                Console.WriteLine("Exception " + e.StackTrace);
            }
        }
    }
}

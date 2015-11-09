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
        static void Main(string[] args)
        {
            const int port = 8080;

            try
            {
                IPAddress ipAD = IPAddress.Parse("127.0.0.1");

                TcpListener listener = new TcpListener(ipAD, port);

                listener.Start();


                Console.WriteLine("Server is listening on port {0}", port);
                Console.WriteLine("Local Endpoint: {0}", listener.LocalEndpoint);
                Console.WriteLine("Waiting for connection....");

                Socket s = listener.AcceptSocket();
                Console.WriteLine("Connection accepted from " + s.RemoteEndPoint);

                byte[] b = new byte[100];
                int k = s.Receive(b);

                char cc = ' ';
                string test = null;
                Console.WriteLine("Recieved...");
                for (int i = 0; i < k - 1; i++)
                {
                    Console.Write(Convert.ToChar(b[i]));
                    cc = Convert.ToChar(b[i]);
                    test += cc.ToString();
                }

                ASCIIEncoding asen = new ASCIIEncoding();
                s.Send(asen.GetBytes("The string was recieved by the server."));
                s.Close();


                Console.ReadLine();

                //goto m;
                //s.Close();
                //myList.Stop();
            } catch (Exception e) {
                Console.WriteLine("Exception " + e.StackTrace);
            }
        }
    }
}

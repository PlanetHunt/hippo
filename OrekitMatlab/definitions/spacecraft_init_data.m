function [a,e,in,omega,raan,mean_anomaly,date,step_size,duration] = spacecraft_init_data(mu,i)


switch i
     case 1 %CanX-4:
        % date: 2014.07.04 16:55:41.240
        n = 14.73345459;
        a = (mu/(n*2*pi/(24*3600))^2)^(1/3);
        e = 0.0012952;
        %e = 0.01;
        in = 98.2557/180*pi;
        omega = 258.543/180*pi;
        raan = 252.93/180*pi;
        mean_anomaly = 101.433/180*pi;
    case 2 %CanX-5:
        % date: 2014.07.04 18:32:30.036
        n = 14.73568733;
        a = (mu/(n*2*pi/(24*3600))^2)^(1/3);
        e = 0.001037;
        in = 98.2534/180*pi;
        omega = 255.869/180*pi;
        raan = 252.995/180*pi;
        mean_anomaly = 104.137;%/180*pi;
    case 3 %UWE-3: %date: 2014.01.01 00:00:00.000
        a = 7019120.191;
        e = 0.007879;
        in =  97.873/180*pi;
        omega = 71.994/180*pi;
        raan = 76.949/180*pi;
        mean_anomaly = 340.151/180*pi;
    case 4 % fun cube 4/12/2013 11:00:00
        a = 7016995.871;	
        e = 0.006422; 
        in = 97.859*pi/180; 
        omega = 145.12*pi/180; 
        raan = 50.485*pi/180; 
        %ta=68.643*pi/180; 
        mean_anomaly = 67.959*pi/180;
        date = [2013, 12, 04, 11, 00, 00.000];
    case 5 % Shaubs deputy - from his paper
        chief = [7555000;0.0500000000000000;0.837758040957278;0.349065850398866;0.174532925199433;2.00616895449115];
        delta = [-100; 0; 0.05*pi/180; 0; -0.01*pi/180; 0];
        deputy = chief+delta;
        a = deputy(1);
        e = deputy(2);
        in = deputy(3);
        omega = deputy(4);
        raan = deputy(5);
        mean_anomaly = deputy(6);
        date = [2014, 01, 01, 00, 00, 00.000];
end
%% Simulator settings:
% date = [2014, 01, 01, 00, 00, 00.000];

% date = [2014,07,04,16,50,32.000];
step_size = 60;
duration = 96*60*1;
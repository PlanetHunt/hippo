function [ oscOe,date ] = setChiefOrbitalElements(chiefNr)

switch chiefNr
    case 1 % hin cube 4/12/2013 11:00:00
        chiefMeanOE = [7017102.334; 0.006535; 97.858*pi/180; 145.60397*pi/180; 50.48597*pi/180; 67.17897*pi/180; 67.871*pi/180];
    
    case 2 % Shaubs chief - from his paper
        
        %Chief OE from Schaubs paper
        chiefMeanOE = [7555000; 0.05; 48*pi/180; 10*pi/180; 20*pi/180; 0; 120*pi/180];
        
        %calc true anomaly as shaub didnt provide it
        %calc true anomaly
        true_anomaly = meanAnomToTrueAnom(chiefMeanOE(2), chiefMeanOE(7) );
        chiefMeanOE(6) = true_anomaly;
        
        
    case 3 %UWE-3 slightly shifted: %date: 2014.01.01 00:00:00.000
        a = 7019120.191+500;
        e = 0.007879+0.002;
        in =  97.873/180*pi;
        omega = 71.994/180*pi+pi/100;
        raan = 76.949/180*pi+pi/100;
        mean_anomaly = 340.151/180*pi+pi/100;
        
        chiefMeanOE = [a;e;in;omega;raan;0;mean_anomaly];

        %calc true anomaly
        true_anomaly = meanAnomToTrueAnom(chiefMeanOE(2), chiefMeanOE(7) );
        chiefMeanOE(6) = true_anomaly;
    case 6
%         oecBase = [7555000;0.0500000000000000;deg2rad(48);deg2rad(10);deg2rad(20);0;deg2rad(120)];
%         oecBase(6) = meanAnomToTrueAnom(oecBase(2), oecBase(7) );
%         
%         delta = [-63.38115; 5.6267e-05; -8.7266e-06; 5.6267e-03; -4.8267e-04; 0; 0];
%         chiefMeanOE = oecBase+delta;
        chiefMeanOE = [7554936.61885000;0.0500562670000000;0.837749314357278;0.180159625199433;0.348583180398866;2.17827223098421;2.09439510239320];
        
        
        date = [2014, 01, 01, 00, 00, 00.000];
    case 7 % hin cube 4/12/2013 11:00:00 osc OE
        chiefMeanOE =[7020518.55375672;0.00607122530109426;1.70791029724478;2.53173538820599;0.881064339677126;1.20632866524041;1.19500225567207];
        date = [2013, 12, 04, 11, 00, 00.000];
    case 8
        %shaubs chief fixed
        [oscOe,MeanOE] = calcInitialOscOeFromMeanOe ([7555000;0.0500000000000000;deg2rad(48);deg2rad(10);deg2rad(20);0;deg2rad(120)])
        date = [2014, 01, 01, 00, 00, 00.000];
end
 %chiefMeanOE=chiefMeanOE';
 oscOe;
end
function [ oscOE ] = convertMeanOeToOscOe( meanOE )
%convertMeanOeToOscOe This is a wrapper function for mean2osc that handles
%meanOE is a vector 7 elements long (a e i omega raan ta ma)

meanOE(1) = meanOE(1)*10^-3; %convert meters to km (this is what osc2mean expects)
meanOE(3) = wrapToPi(wrapToPi(meanOE(3))); %inclination should be between 0 and pi
meanOE(4) = wrapTo2Pi(wrapToPi(meanOE(4))); %omega
meanOE(5) = wrapTo2Pi(wrapToPi(meanOE(5))); %raan
meanOE(6) = wrapTo2Pi(wrapToPi(meanOE(6))); %true anomaly

oscOE = mean2osc(meanOE(1:6)); %osc2mean function from Alex

oscOE(1) = oscOE(1)*10^3;%convert back to meters; 
oscOE(7) = trueAnomToMeanAnom(oscOE(2), oscOE(6));

oscOE = oscOE';
end


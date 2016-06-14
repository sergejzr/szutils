package de.l3s.signal;

public class LowPassFilter {
	void getLPCoefficientsButterworth2Pole( int samplerate,  double cutoff, double[]  ax, double[]  by)
	{
	    double PI      = 3.1415926535897932385;
	    double sqrt2 = 1.4142135623730950488;

	    double QcRaw  = (2 * PI * cutoff) / samplerate; // Find cutoff frequency in [0..PI]
	    double QcWarp = Math.tan(QcRaw); // Warp cutoff frequency

	    double gain = 1 / (1+sqrt2/QcWarp + 2/(QcWarp*QcWarp));
	    by[2] = (1 - sqrt2/QcWarp + 2/(QcWarp*QcWarp)) * gain;
	    by[1] = (2 - 2 * 2/(QcWarp*QcWarp)) * gain;
	    by[0] = 1;
	    ax[0] = 1 * gain;
	    ax[1] = 2 * gain;
	    ax[2] = 1 * gain;
	}






	double xv[]=new double[3];
	double yv[]=new double[3];

	void filter(double[] samples, int count)
	{
	   double ax[]=new double[3];
	   double by[]=new double[3];

	   getLPCoefficientsButterworth2Pole(44100, 5000, ax, by);

	   for (int i=0;i<count;i++)
	   {
	       xv[2] = xv[1]; xv[1] = xv[0];
	       xv[0] = samples[i];
	       yv[2] = yv[1]; yv[1] = yv[0];

	       yv[0] =   (ax[0] * xv[0] + ax[1] * xv[1] + ax[2] * xv[2]
	                    - by[1] * yv[0]
	                    - by[2] * yv[1]);

	       samples[i] = yv[0];
	   }
	}
}

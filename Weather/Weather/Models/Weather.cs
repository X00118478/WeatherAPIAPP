namespace Weather.Models
{
    public class WeatherInfo
    {
        public string City { get; set; }

        public string Conditions { get; set; }

        public double MaxTemperature { get; set; }

        public double MinTemperature { get; set; }

        public string WindDirection { get; set; }

        public int WindSpeed { get; set; }

        public string Outlook { get; set; }
    }
}